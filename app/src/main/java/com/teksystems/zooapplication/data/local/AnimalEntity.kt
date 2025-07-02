package com.teksystems.zooapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val name: String,
    val commonName: String?,
    val phylum: String,
    val scientificName: String? = null,
    val slogan: String?,
    val lifespan: String?,
    val habitat: String?,
    val wingspan: String?,
    val prey: String?,
    val predators: String?,
    val fetchedAt: Long
)
