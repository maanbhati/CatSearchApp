package com.cat.search.ui.savedbreeds

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cat.search.TestCoroutineRule
import com.cat.search.data.model.Breed
import com.cat.search.repository.BreedRepository
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
class SavedBreedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var repository: BreedRepository

    @MockK
    private lateinit var breed: Breed

    private lateinit var savedBreedViewModel: SavedBreedViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        savedBreedViewModel = SavedBreedViewModel(repository)
    }

    @Test
    fun when_get_all_breeds_called_verify_method_call_from_repository() {
        testCoroutineRule.runBlockingTest {
            savedBreedViewModel.getAllBreeds()

            verify { repository.getAllBreeds() }
        }
    }

    @Test
    fun when_on_breed_swiped_called_verify_method_call_from_repository() {
        testCoroutineRule.runBlockingTest {
            savedBreedViewModel.onBreedSwiped(breed)

            verify { runBlocking { repository.deleteBreed(breed) } }
        }
    }

    @Test
    fun when_on_undo_click_called_verify_method_call_from_repository() {
        testCoroutineRule.runBlockingTest {
            savedBreedViewModel.onUndoClick(breed)

            verify { runBlocking { repository.insertBreed(breed) } }
        }
    }

    @Test
    fun when_on_undo_click_called_do_not_call_delete_breed_method_from_repository() {
        testCoroutineRule.runBlockingTest {
            savedBreedViewModel.onUndoClick(breed)

            verify(inverse = true) { runBlocking { repository.deleteBreed(breed) } }
        }
    }

    @Test
    fun when_breed_swiped_called_do_not_call_insert_breed_method_from_repository() {
        testCoroutineRule.runBlockingTest {
            savedBreedViewModel.onBreedSwiped(breed)

            verify(inverse = true) { runBlocking { repository.insertBreed(breed) } }
        }
    }
}