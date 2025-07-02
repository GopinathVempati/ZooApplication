package com.teksystems.zooapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AnimalEntity::class], version = 1, exportSchema = false)
abstract class AnimalDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
}
