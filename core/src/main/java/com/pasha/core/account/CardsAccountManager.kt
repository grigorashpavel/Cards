package com.pasha.core.account

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log


private const val MANAGER_TAG = "CardsAccountManager"


/**
 * Данный класс - оболчка над [AccountManager]. Ее следует использовать только в рамках UI.
 * @param[context] используется для создания [AccountManager], не может быть null
 *
 */
class CardsAccountManager(context: Context) {
    private val manager = AccountManager.get(context)
    val cardsAccounts: Array<Account> get() = manager.getAccountsByType(ACCOUNT_TYPE)
    val activeAccount: Account? get() = fetchActiveAccount()

    /**
     * Создает аккаунт и возращает результат создания.
     * @param[email] почта аккаунта
     * @param[password] пароль аккаунта
     * @param[isTemporary] является ли аккаунт временным (на одну сессию)
     * @param[isActive] является ли аккаунт активным. В один момент может существовать только один
     * активный аккаунт. За логику отвечает разработчик.
     * @return true при удачном создании или false, если аккаунт существовал или возникла ошибка
     *
     */
    fun createAccount(
        email: String,
        password: String,
        isTemporary: Boolean,
        isActive: Boolean
    ): Boolean {
        val account = Account(email, ACCOUNT_TYPE)
        val accountInfo = Bundle().apply {
            putString(IS_ACTIVE_ACCOUNT, isActive.toString())
            putString(IS_SAVED_ACCOUNT, isTemporary.not().toString())
        }

        val canAddAccount = email.isNotEmpty() && password.isNotEmpty()
        if (canAddAccount) {
            val isAdded = manager.addAccountExplicitly(account, password, accountInfo)
            if (isAdded) manager.notifyAccountAuthenticated(account)

            return isAdded
        }

        return false
    }

    /**
     * Сохраняет аккаунт. Аккаунт перестает быть временным.
     * @param[account] целевой аккаунт для сохранения. Уже должен быть создан в системе.
     */
    fun saveAccount(account: Account?) {
        if (account == null) return

        manager.setUserData(account, IS_SAVED_ACCOUNT, true.toString())
    }

    /**
     * Ставит метку аккаунту, что он является активным.
     * @param[account] целевой аккаунт для активации. Уже должен быть создан в системе.
     * В любой момент времени на одном устройстве только один аккаунт может быть активный. За логику
     * отвечает разработчик.
     *
     */
    fun activateAccount(account: Account?) {
        if (account == null) return

        manager.setUserData(account, IS_ACTIVE_ACCOUNT, true.toString())
    }

    /**
     * Деактивирует аккаунт, целевое место вызова - функционал выхода из аккаунт.
     */
    fun exitFromAccount() {
        manager.setUserData(activeAccount, IS_ACTIVE_ACCOUNT, false.toString())
    }

    /**
     * Регистрирует новую пару токенов для аккаунта. Токены будут закешированы системой.
     * @param[accessToken] принимает токен доступа (короткое время жизни)
     * @param[refreshToken] принимает токен обновления (длительное время жизни)
     */
    fun setTokensOnActiveAccount(accessToken: String?, refreshToken: String?) {
        manager.setAuthToken(activeAccount, Authenticator.KEY_ACCESS_TOKEN, accessToken)
        manager.setAuthToken(activeAccount, Authenticator.KEY_REFRESH_TOKEN, refreshToken)
    }

    /**
     * Удаляет закешированные токены.
     * @param[tokens] токены, подлежащие удалению из кеша, любого типа.
     *
     */
    fun invalidateTokens(vararg tokens: String?) {
        val tokns = tokens.filterNotNull()
        for (token in tokns) {
            manager.invalidateAuthToken(ACCOUNT_TYPE, token)
        }
    }

    /**
     * Находит активный аккаунт по внутренним ключам.
     * @return [Account] если активный аккаунт существует, иначе null
     *
     */
    private fun fetchActiveAccount(): Account? {
        for (account in cardsAccounts) {
            val isActive = manager.getUserData(account, IS_ACTIVE_ACCOUNT) == true.toString()
            if (isActive) return account
        }

        return null
    }

    /**
     * Удаляет все временные аккаунты. Использует внутренние метки.
     *
     */
    fun removeTemporaryAccounts() {
        for (account in cardsAccounts) {
            val isTemporary =
                manager.getUserData(account, IS_SAVED_ACCOUNT) != true.toString()
            if (isTemporary) manager.removeAccountExplicitly(account)
        }
    }

    /**
     * Выводит список аккаунтов Cards приложения в logcat-панель.
     * Требуется для первичной отладки.
     */
    fun logCardsAccounts() {
        for (account in cardsAccounts) {
            val isSaved = manager.getUserData(account, IS_SAVED_ACCOUNT)
            val isActive = manager.getUserData(account, IS_ACTIVE_ACCOUNT)

            Log.d(MANAGER_TAG, "Account: ${account.name}[isSaved=$isSaved, isActive=$isActive]")
        }
    }

    /**
     * Получает закешированный токен доступа из системы. Метод асинхронный,
     * напрямую не вызывать на главном потоке. Для получения результата использовать [callback].
     *
     * **!ВАЖНО!** Если не получается получить закешированный токен, то используется [Authenticator],
     * для обновления токенов с сервера. Поэтому метод асинхронный.
     * @param[account] аккаунт, для которого хотим получить токен доступа
     * @param[activity] действие приложения для вызова
     * (на текущий момент используется [MainActivity], так как подход на множестве [Activity] deprecated)
     * @param[callback] объект вложенного класса для получения результата запроса токена.
     * @see[TokenCallback]
     *
     */
    fun getAuthTokenAsync(account: Account, activity: Activity, callback: TokenCallback) {
        manager.getAuthToken(
            account,
            Authenticator.KEY_ACCESS_TOKEN,
            null,
            activity,
            callback,
            null
        )
    }

    /**
     * Синхронный метод получения токена. Следует вызывать, если вы уверены в наличии токена в кеше
     * или вам не требуется дальнейшее его получение с сервера.
     * @param[account] аккаунт для которого нужно получить токен
     * @param[tokenType] тип токена (access - токен доступа, refresh - токен обновления)
     *
     * @return целевой токен или null, если токен не был найден
     *
     */
    fun getAuthTokenSync(account: Account?, tokenType: String?): String? {
        return manager.peekAuthToken(account, tokenType)
    }

    /**
     * Класс для обработки обратного вызова получения токена асинхронно.
     * Логика данного класса на текущий момент сильно привязана к [MainActivity], стоит использовать аккуратно
     * @param[authNavigationCallback] для навигации к auth-feature, происходит выход с аккаунта.
     * . Будет вызван, если выполнено все следующее:
     *  1. Закешированный токен не был найдет
     *  2. [Authenticator] не смог получить новые токены с сервера по причине проблем с токеном
     *  refresh (401 refresh уничтожается) или с агрератором ключей, если используется OAuth
     *
     * @param[indicatorDismissCallback] для отмены индикатора загрузки, если используется
     * @param[showErrorMassageCallback] для вызова диалога показа ошибки
     *
     */
    inner class TokenCallback(
        private val authNavigationCallback: () -> Unit,
        private val indicatorDismissCallback: () -> Unit,
        private val showErrorMassageCallback: (message: String) -> Unit
    ) : AccountManagerCallback<Bundle> {
        /**
         * Единственный метод SAM-интерфейса наследования. Выполняется как результат [AccountManagerFuture]
         * по завершению вызова.
         * @param[future] объект резульата асинхронного вызова
         *
         */
        override fun run(future: AccountManagerFuture<Bundle>?) {
            try {
                val bundle = future?.result
                val errorCode = bundle?.getString(AccountManager.KEY_ERROR_CODE)
                val errorMessage = bundle?.getString(AccountManager.KEY_ERROR_MESSAGE)
                val accessToken = bundle?.getString(AccountManager.KEY_AUTHTOKEN)

                indicatorDismissCallback.invoke()
                if (accessToken != null) {
                    invalidateTokens(accessToken)
                } else if (errorCode != null) {
                    if (errorCode == "401") {
                        authNavigationCallback.invoke()
                        exitFromAccount()
                    }

                    val canShowMessage =
                        errorCode.isNotEmpty() && (errorMessage?.let { it.isNotEmpty() } ?: false)
                    if (canShowMessage) {
                        showErrorMassageCallback.invoke("Code: $errorCode\nMessage: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val IS_ACTIVE_ACCOUNT = "is_active"
        const val IS_SAVED_ACCOUNT = "is_saved"
        const val ACCOUNT_TYPE = "com.pasha.cards"
    }
}