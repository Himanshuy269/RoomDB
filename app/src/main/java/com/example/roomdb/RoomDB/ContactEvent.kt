package com.example.roomdb.RoomDB

sealed interface ContactEvent {
    object SaveContact: ContactEvent
    data class SetFirstName(val firstName:String): ContactEvent
    data class SetLastName(val lastName:String): ContactEvent
    data class SetPhoneNumber(val PhoneNumber :String): ContactEvent
    object showDialog : ContactEvent
    object HideDialog : ContactEvent
    data class SortContact(val sortType: sortType): ContactEvent
    data class DeleteContact(val contact: Contact): ContactEvent

}