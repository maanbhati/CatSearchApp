package com.cat.search.repository

import com.cat.search.data.local.BreedsDao
import com.cat.search.data.model.Breed
import com.cat.search.data.remote.BreedApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedRepository @Inject constructor(
    private val breedApi: BreedApi,
    private val breedsDao: BreedsDao
) {
    suspend fun getCatBreeds() = breedApi.getCatBreeds()

    suspend fun searchBreeds(searchQuery: String, pageNumber: Int) =
        breedApi.searchCatBreeds(searchQuery, pageNumber)

    fun getAllBreeds() = breedsDao.getBreeds()

    suspend fun insertBreed(breed: Breed) = breedsDao.insertBreed(breed)

    suspend fun deleteBreed(breed: Breed) = breedsDao.deleteBreed(breed)
}