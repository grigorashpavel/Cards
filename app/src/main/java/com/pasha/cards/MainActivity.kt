package com.pasha.cards

import android.accounts.AccountManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.pasha.cards.databinding.ActivityMainBinding
import com.pasha.core.account.AccountHost
import com.pasha.core.account.AuthenticatorService
import com.pasha.core.shared.SharedApplicationViewModel

private const val ACTIVITY_TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val service = AuthenticatorService()

        //if (ContextCompat.checkSelfPermission(this, ))

        val manager = AccountHost(accountManager = AccountManager.get(baseContext))
        manager.createAccount()

//        val applicationViewModel by viewModels<SharedApplicationViewModel>()
//        applicationViewModel.bottomNavigationId = binding.bottomNavigationView.id
//
//        val navigationController = binding.navHostFragment
//            .getFragment<NavHostFragment>().navController
//
//        val isAuthorized = false
//        if (!isAuthorized && savedInstanceState == null) {
//            navigationController.navigate(R.id.auth_feature_nested_graph)
//        }
//        binding.bottomNavigationView.setupWithNavController(navigationController)

        Log.d(ACTIVITY_TAG, "fun onCreate()")
    }
}