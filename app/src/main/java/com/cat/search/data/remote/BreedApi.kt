package com.cat.search.data.remote

import com.cat.search.data.model.BreedResponse
import com.cat.search.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BreedApi {

    @GET("breeds")
    suspend fun getCatBreeds(
        @Header("x-api-key") apiKey: String = API_KEY
    ): Response<BreedResponse>

    @GET("breeds/search")
    suspend fun searchCatBreeds(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Header("x-api-key") apiKey: String = API_KEY
    ): Response<BreedResponse>
}