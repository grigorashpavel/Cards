package com.pasha.all_cards.internal.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pasha.all_cards.internal.domain.repositories.AllCardsRepository
import com.pasha.all_cards.internal.domain.models.Card
import com.pasha.core.network.api.utils.Response
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AllCardsViewModel(
    private val allCardsRepository: AllCardsRepository
) : ViewModel() {
    private val _cards: MutableLiveData<List<Card>> = MutableLiveData(listOf())
    val cards: LiveData<List<Card>> get() = _cards

    private val _searchedCards: MutableLiveData<List<Card>> = MutableLiveData(listOf())
    val searchedCards: LiveData<List<Card>> get() = _searchedCards

    private val _uiState = MutableLiveData(AllCardsState())
    val uiState: LiveData<AllCardsState> get() = _uiState

    fun fetchCards() {
        allCardsRepository.getAllCards().onEach { response ->
            when (response) {
                is Response.Loading -> {
                    _uiState.value = _uiState.value?.copy(isLoading = true)
                }

                is Response.Success -> {
                    _uiState.value = _uiState.value?.copy(isLoading = false)

                    _cards.value = response.data
                }

                is Response.Error -> {
                    _uiState.value = _uiState.value?.copy(isLoading = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun fetchCardsByName(queryName: String) {
        allCardsRepository.getCardsByName(queryName).onEach { response ->
            when (response) {
                is Response.Loading -> {
                    _uiState.value = _uiState.value?.copy(isLoading = true)
                }

                is Response.Success -> {
                    _uiState.value = _uiState.value?.copy(isLoading = false)

                    _searchedCards.value = response.data
                }

                is Response.Error -> {
                    _uiState.value = _uiState.value?.copy(isLoading = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    class Factory @Inject constructor(
        private val allCardsRepository: AllCardsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AllCardsViewModel(allCardsRepository) as T
        }
    }
}