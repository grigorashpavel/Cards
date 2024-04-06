package com.pasha.all_cards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.google.android.material.search.SearchView
import com.pasha.all_cards.databinding.FragmentAllCardsBinding

class AllCardsFragment : Fragment() {
    private var _binding: FragmentAllCardsBinding? = null
    private val binding get() = _binding!!

    private val QUERY_TAG = "QUERY_OF_SEARCHING"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (savedInstanceState != null) {
//            val savedQuery = savedInstanceState.getString(QUERY_TAG)
//            when {
//                binding.searchView.isShown -> binding.searchView.setText(savedQuery)
//                else -> binding.searchBar.setText(savedQuery)
//            }
//        }

        setupSearchView()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        val query = if (binding.searchView.isShown) binding.searchView.text
//        else binding.searchBar.text
//
//       if (query.isNotEmpty()) outState.putString(QUERY_TAG, query.toString())
//    }

    private fun setupSearchView() {
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
                callback.isEnabled = true
            }
            if (newState == SearchView.TransitionState.HIDING) {
                binding.searchBar.setText(binding.searchView.text)
                callback.isEnabled = false
            }
        }
    }
}