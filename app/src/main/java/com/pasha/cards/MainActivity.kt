package com.pasha.cards

import android.accounts.Account
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pasha.cards.databinding.ActivityMainBinding
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core.ui_deps.ActivityUiDeps


private const val ACTIVITY_TAG = "MAIN_ACTIVITY"


class MainActivity : AppCompatActivity(), ActivityUiDeps {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ACTIVITY_TAG, "fun onCreate()")


        val manager = CardsAccountManager(this)
        manager.logCardsAccounts()
        manager.removeTemporaryAccounts()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationController =
            binding.navHostFragment.getFragment<NavHostFragment>().navController
        binding.bottomNavigationView.setupWithNavController(navigationController)


        if (isOpenedByAuthenticatorToCreateAccount()) {
            navigationController.navigate(R.id.auth_feature_nested_graph)
            return
        }

        val isFirstCreation = (savedInstanceState == null)
        if (isFirstCreation) {
            val indicator = ProgressIndicator()
            val tokenCallback = manager.TokenCallback(
                indicatorDismissCallback = {
                    if (indicator.isResumed) indicator.dismiss()
                },
                authNavigationCallback = {
                    // Осталось поймать момент когда ключ refresh уничтожается
                    navigationController.navigate(R.id.auth_feature_nested_graph)
                },
                showErrorMassageCallback = ::showErrorMessage
            )

            val activeAccount = manager.activeAccount
            if (activeAccount != null) {
                manager.getAuthTokenAsync(activeAccount, this, tokenCallback)
            } else {
                navigationController.navigate(R.id.auth_feature_nested_graph)
            }
        }
    }

    private fun List<Account>.pickAccount(callback: (Int) -> Unit) {
        if (isEmpty()) return

        val accountNames = this.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(
            this@MainActivity,
            com.pasha.core_ui.R.style.Theme_Pasha_MaterialAlertDialog_Centered
        )
            .setTitle("Аккаунт для входа:")
            .setCancelable(true)
            .setItems(accountNames) { _, which -> callback(which) }
            .show()
    }

    override fun showErrorMessage(message: String) {
        MaterialAlertDialogBuilder(
            this,
            com.pasha.core_ui.R.style.Theme_Pasha_MaterialAlertDialog_Centered
        )
            .setTitle("Ошибка")
            .setMessage(message)
            .setNeutralButton(android.R.string.ok) { _, _ ->

            }
            .show()

    }

    override fun offerToAddAccount(callback: () -> Unit) {
        Snackbar.make(binding.root, "Save Account?", Snackbar.LENGTH_LONG)
            .setAction(android.R.string.ok) {
                callback.invoke()
            }.show()
    }

    override fun isOpenedByAuthenticatorToCreateAccount(): Boolean {
        return intent.action == "com.pasha.cards.ADD_ACCOUNT"
    }

    override fun hideBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    override fun showBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }
}