package com.teksystems.zooapplication.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimalApiService {
    @GET("animals")
    suspend fun getAnimals(@Query("name") name: String): Response<List<AnimalItem>>
}