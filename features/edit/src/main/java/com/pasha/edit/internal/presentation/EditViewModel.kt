package com.pasha.edit.internal.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pasha.core.cards.CardsManager
import com.pasha.core.network.api.utils.Response
import com.pasha.edit.internal.domain.repositories.EditRepository
import ezvcard.VCard
import ezvcard.VCardVersion
import ezvcard.io.text.VCardWriter
import ezvcard.property.Email
import ezvcard.property.StructuredName
import ezvcard.property.Telephone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.StringWriter
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class EditViewModel(
    private val editRepository: EditRepository
) : ViewModel() {

    private val vcard = VCard().apply {
        version = VCardVersion.V3_0
    }
    private val _uiVCard: MutableLiveData<String> = MutableLiveData("")
    val uiVCard: LiveData<String> get() = _uiVCard

    val editableState: MutableLiveData<EditableState> =
        MutableLiveData<EditableState>(EditableState())

    private val _uiState: MutableLiveData<EditState> = MutableLiveData(EditState())
    val uiState: LiveData<EditState> get() = _uiState

    private var lastTask: Job? = null

    fun recreateVCard() {
        val structName = StructuredName()
        structName.family = editableState.value?.family
        structName.given = editableState.value?.name
        vcard.structuredName = structName

        val email = Email(editableState.value?.email)
        vcard.emails.clear()
        vcard.emails.add(email)

        val telephone = Telephone(editableState.value?.phone)
        telephone.types.clear()
        vcard.telephoneNumbers.clear()
        vcard.telephoneNumbers.add(telephone)
    }

    fun recreateUi() {
        viewModelScope.launch(Dispatchers.IO) {
            val string = CardsManager.cardToString(vcard)
            withContext(Dispatchers.Main) {
                _uiVCard.value = string ?: ""
            }
        }
    }

    fun uploadVCard(cardName: String, currentTime: Long) {
        lastTask = editRepository.uploadCard(cardName, vcard, currentTime).onEach { response ->
            if (coroutineContext.isActive.not()) return@onEach

            when (response) {
                is Response.Loading -> {
                    _uiState.value = _uiState.value?.copy(isLoading = true)
                }

                is Response.Success -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        isUploaded = true
                    )
                }

                is Response.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorCode = response.code,
                        errorMessage = response.errorMessage
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun refreshEditState() {
        _uiState.value = EditState()
    }

    fun cancelTask() {
        lastTask?.cancel()
        lastTask = null
    }

    class Factory @Inject constructor(
        private val editRepository: EditRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditViewModel(editRepository) as T
        }
    }
}