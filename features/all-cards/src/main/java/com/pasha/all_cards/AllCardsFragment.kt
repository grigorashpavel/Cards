package com.pasha.all_cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.google.android.material.search.SearchView
import com.pasha.all_cards.databinding.FragmentAllCardsBinding
import com.pasha.core.ui_deps.ActivityUiDeps


private const val QUERY_TAG = "QUERY_OF_SEARCHING"

class AllCardsFragment : Fragment() {
    private var _binding: FragmentAllCardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var uiDepsProvider: ActivityUiDeps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiDepsProvider = requireContext() as ActivityUiDeps
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiDepsProvider.showBottomNavigationView()
//        if (savedInstanceState != null) {
//            val savedQuery = savedInstanceState.getString(QUERY_TAG)
//            when {
//                binding.searchView.isShown -> binding.searchView.setText(savedQuery)
//                else -> binding.searchBar.setText(savedQuery)
//            }
//        }

        setupSearchView { isHiding ->
            if (isHiding) {
                uiDepsProvider.showBottomNavigationView()
            } else {
                uiDepsProvider.hideBottomNavigationView()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        val query = if (binding.searchView.isShown) binding.searchView.text
//        else binding.searchBar.text
//
//       if (query.isNotEmpty()) outState.putString(QUERY_TAG, query.toString())
//    }

    private fun setupSearchView(eventStateChangeCallback: (Boolean) -> Unit) {
        val callback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                binding.searchView.hide()
            }
        callback.isEnabled = false

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchView.hide()
            false
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                eventStateChangeCallback(false)
                callback.isEnabled = true
            }
            if (newState == SearchView.TransitionState.HIDING) {
                binding.searchBar.setText(binding.searchView.text)
                eventStateChangeCallback(true)
                callback.isEnabled = false
            }
        }
    }
}