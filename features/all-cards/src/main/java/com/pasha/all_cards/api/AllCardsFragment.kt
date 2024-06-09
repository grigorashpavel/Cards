package com.pasha.all_cards.api

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pasha.all_cards.R
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchView
import com.pasha.all_cards.databinding.FragmentAllCardsBinding
import com.pasha.all_cards.internal.di.DaggerAllCardsComponent
import com.pasha.all_cards.internal.presentation.AllCardsRecyclerAdapter
import com.pasha.all_cards.internal.presentation.AllCardsViewModel
import com.pasha.all_cards.internal.presentation.HorizontalSpacingItemDecoration
import com.pasha.all_cards.internal.presentation.HorizontalRecyclerAdapter
import com.pasha.all_cards.internal.presentation.SpacingItemDecoration
import com.pasha.all_cards.internal.presentation.ViewerViewModel
import com.pasha.core.di.findDependencies
import com.pasha.core.ui_deps.ActivityUiDeps
import java.util.UUID
import javax.inject.Inject

const val ID_KEY = "card_id"

class AllCardsFragment : Fragment() {
    private var _binding: FragmentAllCardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var uiDepsProvider: ActivityUiDeps

    private lateinit var mainAdapter: AllCardsRecyclerAdapter
    private lateinit var searchAdapter: AllCardsRecyclerAdapter
    private lateinit var postSearchAdapter: HorizontalRecyclerAdapter

    @Inject
    internal lateinit var viewmodelFactory: AllCardsViewModel.Factory
    private val viewModel: AllCardsViewModel by viewModels { viewmodelFactory }

    @Inject
    internal lateinit var viewerViewmodelFactory: ViewerViewModel.Factory
    private val viewerViewModel: ViewerViewModel by activityViewModels<ViewerViewModel> { viewerViewmodelFactory }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerAllCardsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiDepsProvider = requireContext() as ActivityUiDeps
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiDepsProvider.showBottomNavigationView()
        createAdapters()
        viewModel.fetchCards()

        setupSearchView { isHiding ->
            if (isHiding) {
                uiDepsProvider.showBottomNavigationView()
            } else {
                uiDepsProvider.hideBottomNavigationView()
            }
        }

        binding.rvAllCards.setupVerticalRecyclerView(mainAdapter, SpacingItemDecoration(32))
        viewModel.cards.observe(viewLifecycleOwner) { cards ->
            mainAdapter.setCards(cards)
        }
        binding.rvSearch.setupVerticalRecyclerView(searchAdapter, SpacingItemDecoration(32))
        binding.rvPostCards.setupHorizontalRecyclerView(
            postSearchAdapter,
            HorizontalSpacingItemDecoration(32)
        )
        viewModel.searchedCards.observe(viewLifecycleOwner) { searchCards ->
            searchAdapter.setCards(searchCards)
            postSearchAdapter.setCards(searchCards)
        }
    }

    private fun RecyclerView.setupVerticalRecyclerView(
        adapter: AllCardsRecyclerAdapter,
        decor: RecyclerView.ItemDecoration
    ) {
        layoutManager = LinearLayoutManager(requireContext())
        this.adapter = adapter
        addItemDecoration(decor)
    }

    private fun RecyclerView.setupHorizontalRecyclerView(
        adapter: HorizontalRecyclerAdapter,
        decor: RecyclerView.ItemDecoration
    ) {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        this.adapter = adapter
        addItemDecoration(decor)
    }

    private fun createAdapters() {
        mainAdapter = AllCardsRecyclerAdapter(
            onCardClick = { card ->
                navigateToViewer(cardId = card.cardId)
            },
            onDeleteClick = { card ->

            }
        )
        searchAdapter = AllCardsRecyclerAdapter(
            onCardClick = { card ->
                navigateToViewer(cardId = card.cardId)
            },
            onDeleteClick = { card ->

            }
        )
        postSearchAdapter = HorizontalRecyclerAdapter(
            onCardClick = { card ->
                navigateToViewer(cardId = card.cardId)
            },
            onDeleteClick = { card ->

            }
        )
    }

    private fun navigateToViewer(cardId: UUID) {
        viewerViewModel.clearCardData()
        viewerViewModel.fetchCard(cardId)
        viewerViewModel.setId(cardId)
        findNavController().navigate(R.id.action_allCardsFragment_to_viewerFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSearchView(eventStateChangeCallback: (Boolean) -> Unit) {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            binding.searchView.hide()
        }
        callback.isEnabled = false

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchView.hide()
            false
        }

        val queryListener = DelayedQueryTextListener(delayMillis = 2000, onTextChanged = { query ->
            viewModel.fetchCardsByName(query)
        })
        binding.searchView.editText.addTextChangedListener(queryListener)

        binding.searchView.addTransitionListener { searchView, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                eventStateChangeCallback(false)
                callback.isEnabled = true
                uiDepsProvider.switchBackPressedNavigationMode()
            }
            val isHiding = newState == SearchView.TransitionState.HIDING
            val query = searchView.editText.text.toString()

            if (isHiding) {
                binding.searchBar.setText(binding.searchView.text)
                eventStateChangeCallback(true)
                callback.isEnabled = false
                uiDepsProvider.switchBackPressedNavigationMode()

                if (query.isNotEmpty() && queryListener.isWaiting()) {
                    queryListener.cancelTask()
                    viewModel.fetchCardsByName(query)
                }
            }
        }

        setupLoadingListener()
    }

    private fun setupLoadingListener() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                binding.progressIndicator.visibility = View.VISIBLE
            } else {
                binding.progressIndicator.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    inner class DelayedQueryTextListener(
        private val delayMillis: Long, private val onTextChanged: (String) -> Unit
    ) : TextWatcher {

        private val handler = Handler(Looper.getMainLooper())
        private var runnable: Runnable? = null

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (runnable != null) {
                cancelTask()
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            val query = editable.toString()
            if (query.isNotEmpty()) {
                runnable = Runnable {
                    onTextChanged(editable.toString())
                }
                handler.postDelayed(runnable!!, delayMillis)
            }
        }

        fun cancelTask() {
            handler.removeCallbacks(runnable!!)
            runnable = null
        }

        fun isWaiting() = runnable != null
    }
}