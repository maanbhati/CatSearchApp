package com.cat.search.ui.savedbreeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cat.search.data.model.Breed
import com.cat.search.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedBreedViewModel @Inject constructor(
    private val breedRepository: BreedRepository
) : ViewModel() {

    private val savedBreedEventChannel = Channel<SavedBreedEvent>()
    val savedBreedEvent = savedBreedEventChannel.receiveAsFlow()

    fun getAllBreeds() = breedRepository.getAllBreeds()

    fun onBreedSwiped(breed: Breed) {
        viewModelScope.launch {
            breedRepository.deleteBreed(breed)
            savedBreedEventChannel.send(SavedBreedEvent.ShowUndoDeleteBreedMessage(breed))
        }
    }

    fun onUndoClick(breed: Breed) {
        viewModelScope.launch {
            breedRepository.insertBreed(breed)
        }
    }

    sealed class SavedBreedEvent {
        data class ShowUndoDeleteBreedMessage(val breed: Breed) : SavedBreedEvent()
    }
}