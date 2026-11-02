package com.example.roomdb.RoomDB

data class ContactState(
    val contact: List<Contact> = emptyList(),
    val firstName: String = "",
    val lastName : String = "",
    val phoneNumber: String = "",
    val isAddingcontact: Boolean = false,
    val SortType: sortType = sortType.FIRST_NAME
)
