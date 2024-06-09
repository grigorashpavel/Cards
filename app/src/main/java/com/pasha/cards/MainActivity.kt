package com.pasha.cards

import android.accounts.Account
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pasha.auth.R
import com.pasha.cards.databinding.ActivityMainBinding
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.progress_indicator.api.ProgressIndicator
import com.pasha.core.ui_deps.ActivityUiDeps
import retrofit2.http.Header


private const val ACTIVITY_TAG = "MAIN_ACTIVITY"
private const val AUTH_DEST_LABEL = "fragment_sign_in"

class MainActivity : AppCompatActivity(), ActivityUiDeps {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var navCallBack: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ACTIVITY_TAG, "fun onCreate()")

        val manager = CardsAccountManager(this)
        manager.logCardsAccounts()
        manager.removeTemporaryAccounts()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController =
            binding.navHostFragment.getFragment<NavHostFragment>().navController
        binding.bottomNavigationView.setupWithNavController(navController)

        setupCustomBackPressNavigation()

        if (isOpenedByAuthenticatorToCreateAccount()) {
            navigateToAuth()
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
                    navigateToAuth()
                },
                showErrorMassageCallback = {
                    showMessage(message = it)
                }
            )

            val activeAccount = manager.activeAccount
            if (activeAccount != null) {
                manager.getAuthTokenAsyncByCallback(activeAccount, this, tokenCallback)
            } else {
                navigateToAuth()
            }
        }
    }

    private fun navigateToAuth() {
        navController.popBackStack(R.id.auth, true)
        navController.navigate(R.id.auth)
    }

    private fun setupCustomBackPressNavigation() {
        navCallBack = object : OnBackPressedCallback(true) {
            @SuppressLint("RestrictedApi")
            override fun handleOnBackPressed() {
                val timeToAuth = navController.currentDestination?.label == AUTH_DEST_LABEL
                if (timeToAuth) finish()

                val canNavigateUp = navController.navigateUp()
                if (canNavigateUp) return else finish()
            }

        }

        onBackPressedDispatcher.addCallback(this, navCallBack)
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

    override fun showMessage(header: String, message: String) {
        MaterialAlertDialogBuilder(
            this,
            com.pasha.core_ui.R.style.Theme_Pasha_MaterialAlertDialog_Centered
        )
            .setTitle(header)
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

    override fun checkForAuthNavigation(errorCode: Int) {
        if (errorCode == 401) {
            exitToAuth()
        }
    }

    override fun checkForAuthNavigation(errorCode: String) {
        if (errorCode == "401") {
            exitToAuth()
        }

        val hasUnauthorizedAsErrorMsg = errorCode.contains("401")
        if (hasUnauthorizedAsErrorMsg) {
            exitToAuth()
        }
    }

    override fun switchBackPressedNavigationMode() {
        navCallBack.isEnabled = navCallBack.isEnabled.not()
    }

    private fun exitToAuth() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.auth, true)
            .build()

        navController.navigate(R.id.auth, null, navOptions = navOptions)
    }
}
