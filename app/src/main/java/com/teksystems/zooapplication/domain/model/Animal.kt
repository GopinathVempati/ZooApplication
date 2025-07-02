package com.teksystems.zooapplication.domain.model

data class Animal(
    val name: String,
    val commonName: String,
    val phylum: String,
    val scientificName: String? = null,
    val category: String,
    val slogan: String? = null,
    val lifespan: String? = null,
    val habitat: String? = null,
    val wingspan: String? = null,
    val prey: String? = null,
    val predators: String? = null
)
