package com.cat.search.ui.breeds

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cat.search.R
import com.cat.search.data.model.BreedResponse
import com.cat.search.repository.BreedRepository
import com.cat.search.util.Resource
import com.cat.search.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val catBreedResponse: MutableLiveData<Resource<BreedResponse>> = MutableLiveData()

    init {
        getCatBreeds()
    }

    private fun getCatBreeds() = viewModelScope.launch {
        safeCatBreedsCall()
    }

    private suspend fun safeCatBreedsCall() {
        catBreedResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(context)) {
                val response = breedRepository.getCatBreeds()
                catBreedResponse.postValue(handleBreedResponse(response))
            } else {
                catBreedResponse.postValue(Resource.Error(context.getString(R.string.no_network_connection)))
            }
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> catBreedResponse.postValue(Resource.Error(context.getString(R.string.network_failure)))
                else -> catBreedResponse.postValue(Resource.Error(context.getString(R.string.error_in_data_conversion)))
            }
        }
    }

    private fun handleBreedResponse(response: Response<BreedResponse>): Resource<BreedResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}