package com.example.roomdb.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdb.RoomDB.Contact
import com.example.roomdb.RoomDB.ContactDao
import com.example.roomdb.RoomDB.ContactEvent
import com.example.roomdb.RoomDB.ContactState
import com.example.roomdb.RoomDB.sortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val dao: ContactDao
): ViewModel() {
    private val _sortType = MutableStateFlow(sortType.FIRST_NAME)
    private val _contact = _sortType
        .flatMapLatest { SortType ->
            when(SortType){
                sortType.FIRST_NAME -> dao.getContactOrderedByFirstName()
                sortType.LAST_NAME -> dao.getContactOrderedByLastName()
                sortType.PHONE_NUMBER -> dao.getContactOrderedByPhoneNumber()

            }
        }
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(),emptyList())
    private val _state = MutableStateFlow(ContactState())
    val state = combine(_state,_sortType,_contact){ state,SortType,contact ->
        state.copy(
            contact = contact,
            SortType =SortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }

            ContactEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingcontact = false
                    )
                }
            }
            ContactEvent.SaveContact -> {
                    val firstName = state.value.firstName
                    val lastName = state.value.lastName
                    val phoneNumber = state.value.phoneNumber
                if(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()){
                    return
                }
                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                viewModelScope.launch{
                    dao.upsertContact(contact )
                }
                _state.update { it.copy(
                    isAddingcontact = false,
                    firstName = "",
                    lastName = "",
                    phoneNumber = ""
                ) }
        }
            is ContactEvent.SetFirstName -> {
                _state.update {
                    it.copy(
                        firstName = event.firstName
                    )
                }
            }

            is ContactEvent.SetLastName -> {
                _state.update {
                    it.copy(
                        lastName = event.lastName
                    )
                }
            }

            is ContactEvent.SetPhoneNumber -> {
                _state.update {
                    it.copy(
                        phoneNumber = event.PhoneNumber
                    )
                }
            }
                ContactEvent.showDialog -> {
                    _state.update {
                        it.copy(
                            isAddingcontact = true
                        )
                    }
                }
                is ContactEvent.SortContact -> {
                    _sortType.value = event.sortType
                }
            }
        }
    }
