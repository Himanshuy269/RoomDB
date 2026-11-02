package com.example.roomdb.RoomDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Contact::class],
    version = 1
)
abstract class ContactDB: RoomDatabase() {
    abstract val dao : ContactDao
}