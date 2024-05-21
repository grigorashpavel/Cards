package com.pasha.all_cards

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchView
import com.pasha.all_cards.databinding.FragmentAllCardsBinding
import com.pasha.core.shared.SharedApplicationViewModel


private const val QUERY_TAG = "QUERY_OF_SEARCHING"

class AllCardsFragment : Fragment() {
    private var _binding: FragmentAllCardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val applicationViewModel by activityViewModels<SharedApplicationViewModel>()
        val id = applicationViewModel.bottomNavigationId

        val bottomNavigationView = id?.let {
            requireActivity().findViewById<BottomNavigationView>(it)
        }
        bottomNavigationView?.show()
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
//        if (savedInstanceState != null) {
//            val savedQuery = savedInstanceState.getString(QUERY_TAG)
//            when {
//                binding.searchView.isShown -> binding.searchView.setText(savedQuery)
//                else -> binding.searchBar.setText(savedQuery)
//            }
//        }

        setupSearchView { isHiding ->
            if (isHiding) bottomNavigationView.show() else bottomNavigationView.hide()
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

    private fun BottomNavigationView.hide() {
        visibility = View.GONE
    }

    private fun BottomNavigationView.show() {
        visibility = View.VISIBLE
    }
}