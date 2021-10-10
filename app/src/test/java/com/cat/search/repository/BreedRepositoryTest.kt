package com.cat.search.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cat.search.data.local.BreedsDao
import com.cat.search.data.model.Breed
import com.cat.search.data.remote.BreedApi
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BreedRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var breedApi: BreedApi

    @MockK
    private lateinit var breedsDao: BreedsDao

    @MockK
    private lateinit var breed: Breed

    private lateinit var breedRepository: BreedRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        breedRepository = BreedRepository(breedApi, breedsDao)
    }

    @Test
    fun when_get_all_breeds_called_verify_method_call_from_dao() {
        breedRepository.getAllBreeds()

        verify { breedsDao.getBreeds() }
    }

    @Test
    fun when_get_cat_breeds_called_verify_method_call_from_api() {
        runBlocking {
            breedRepository.getCatBreeds()

            verify { runBlocking { breedApi.getCatBreeds() } }
        }
    }

    @Test
    fun when_search_breeds_with_query_and_page_param_called_verify_method_call_from_api() {
        val query = "query"
        val page = 2
        runBlocking {
            breedRepository.searchBreeds(query, page)

            verify { runBlocking { breedApi.searchCatBreeds(query, page) } }
        }
    }

    @Test
    fun when_insert_breed_called_verify_method_call_from_dao() {
        runBlocking {
            breedRepository.insertBreed(breed)

            verify { runBlocking { breedsDao.insertBreed(breed) } }
        }
    }

    @Test
    fun when_delete_breed_called_verify_method_call_from_dao() {
        runBlocking {
            breedRepository.deleteBreed(breed)

            verify { runBlocking { breedsDao.deleteBreed(breed) } }
        }
    }
}