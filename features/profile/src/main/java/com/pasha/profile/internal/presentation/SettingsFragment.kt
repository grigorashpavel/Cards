package com.pasha.profile.internal.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasha.core.account.CardsAccountManager
import com.pasha.core.di.findDependencies
import com.pasha.core.ui_deps.ActivityUiDeps
import com.pasha.profile.R
import com.pasha.profile.api.PreferencesManager
import com.pasha.profile.databinding.FragmentSettingsBinding
import com.pasha.profile.internal.di.DaggerProfileComponent
import javax.inject.Inject


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    internal lateinit var viewmodelFactory: ProfileViewModel.Factory
    private val viewModel: ProfileViewModel by viewModels { viewmodelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerProfileComponent.factory()
            .create(findDependencies(), requireContext())
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.rvDevices.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        val adapter = SessionRecyclerViewAdapter()
        binding.rvDevices.adapter = adapter

        val preferences = PreferencesManager(requireActivity().applicationContext)
        binding.switchTheme.isChecked = preferences.isDarkTheme
        setSwitchUi(isChecked = preferences.isDarkTheme)

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            setSwitchUi(isChecked)

            preferences.setThemeType(isDark = isChecked)
            AppCompatDelegate.setDefaultNightMode(preferences.themeCode)
        }

        binding.btnDevices.setOnClickListener {
            viewModel.switchSessionsState()
        }

        viewModel.isShowSessions.observe(viewLifecycleOwner) { isShown ->
            if (isShown) {
                viewModel.getActiveDevices()
                showSessionsControl()
            } else {
                hideSessionsControl()
            }
        }

        viewModel.devices.observe(viewLifecycleOwner) { devices ->
            if (devices != null) {
                adapter.setItems(devices)
            }
        }

        binding.btnKillCurrentSession.setOnClickListener {
            viewModel.killCurrentSession()
            val exitCode = 401
            (requireActivity() as ActivityUiDeps).checkForAuthNavigation(exitCode)
        }
        binding.btnKillOtherSessions.setOnClickListener {
            viewModel.killOtherSessions()
        }
    }

    private fun showSessionsControl() {
        binding.btnKillCurrentSession.visibility = View.VISIBLE
        binding.btnKillOtherSessions.visibility = View.VISIBLE
        binding.rvDevices.visibility = View.VISIBLE
    }

    private fun hideSessionsControl() {
        binding.btnKillCurrentSession.visibility = View.GONE
        binding.btnKillOtherSessions.visibility = View.GONE
        binding.rvDevices.visibility = View.GONE
    }


    private fun setSwitchUi(isChecked: Boolean) {
        binding.switchTheme.thumbIconDrawable = if (isChecked) {
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_dark_mode_24)
        } else {
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_light_mode_24)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}