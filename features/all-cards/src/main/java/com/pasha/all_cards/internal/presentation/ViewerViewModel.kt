package com.pasha.all_cards.internal.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pasha.all_cards.internal.domain.models.CardData
import com.pasha.all_cards.internal.domain.repositories.AllCardsRepository
import com.pasha.core.network.api.utils.Response
import ezvcard.VCard
import ezvcard.property.StructuredName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

class ViewerViewModel(
    private val allCardsRepository: AllCardsRepository
) : ViewModel() {
    private var vCard: VCard? = null
    private val _cardData = MutableLiveData(CardData())
    val cardData: LiveData<CardData> get() = _cardData
    private var cardId: UUID? = null

    private val _uiState = MutableLiveData(ViewerState())
    val uiState: LiveData<ViewerState> get() = _uiState

    private var _isDeleted = MutableLiveData<Boolean>(false)
    val isDeleted: LiveData<Boolean> get() = _isDeleted


    fun fetchCard(cardId: UUID) {
        allCardsRepository.getCardById(cardId).onEach { response ->
            when (response) {
                is Response.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorCode = response.code,
                        errorMessage = response.errorMessage
                    )
                }

                is Response.Loading -> {
                    _uiState.value = _uiState.value?.copy(isLoading = true)
                }

                is Response.Success -> {
                    _uiState.value = _uiState.value?.copy(isLoading = false)
                    vCard = response.data
                    vCard?.updateCardData()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun removeCard() {
        cardId?.let { it ->
            allCardsRepository.deleteCardById(it).onEach { response ->
                when (response) {
                    is Response.Success -> {
                        _isDeleted.value = true
                        _isDeleted.value = false
                    }

                    is Response.Error -> {}
                    is Response.Loading -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun clearUiState() {
        _uiState.value = ViewerState()
    }

    fun clearCardData() {
        _cardData.value = CardData()
        cardId = null
    }

    fun setId(id: UUID) {
        cardId = id
    }

    private fun VCard.updateCardData() {
        val name: StructuredName? = getProperty(StructuredName::class.java)
        val email: String? = emails.firstOrNull()?.value
        val phone: String? = telephoneNumbers.firstOrNull()?.text

        _cardData.value = CardData(
            family = name?.family ?: "",
            name = name?.given ?: "",
            email = email ?: "",
            phone = phone ?: ""
        )
    }

    class Factory @Inject constructor(
        private val allCardsRepository: AllCardsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ViewerViewModel(allCardsRepository) as T
        }
    }
}