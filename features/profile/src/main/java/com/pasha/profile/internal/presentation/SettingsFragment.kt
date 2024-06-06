package com.pasha.profile.internal.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import com.pasha.profile.R
import com.pasha.profile.api.PreferencesManager
import com.pasha.profile.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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

        val preferences = PreferencesManager(requireActivity().applicationContext)
        binding.switchTheme.isChecked = preferences.isDarkTheme
        setSwitchUi(isChecked = preferences.isDarkTheme)

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            setSwitchUi(isChecked)

            preferences.setThemeType(isDark = isChecked)
            AppCompatDelegate.setDefaultNightMode(preferences.themeCode)
        }
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