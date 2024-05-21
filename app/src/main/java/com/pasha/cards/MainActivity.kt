package com.pasha.cards

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.pasha.cards.databinding.ActivityMainBinding
import com.pasha.core.shared.SharedApplicationViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val applicationViewModel by viewModels<SharedApplicationViewModel>()
        applicationViewModel.bottomNavigationId = binding.bottomNavigationView.id

        val navigationController = binding.navHostFragment
            .getFragment<NavHostFragment>().navController

        val isAuthorized = false
        if (!isAuthorized) navigationController.navigate(com.pasha.core_ui.R.id.auth_feature_nested_graph)
        binding.bottomNavigationView.setupWithNavController(navigationController)
    }
}