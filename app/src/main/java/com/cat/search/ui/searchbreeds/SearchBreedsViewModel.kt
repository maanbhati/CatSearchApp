package com.cat.search.ui.searchbreeds

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
class SearchBreedsViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val searchBreeds: MutableLiveData<Resource<BreedResponse>> = MutableLiveData()
    var searchBreedsPage = 1

    fun searchBreeds(searchQuery: String) = viewModelScope.launch {
        safeSearchBreedsCall(searchQuery, searchBreedsPage)
    }

    private suspend fun safeSearchBreedsCall(searchQuery: String, searchBreedPage: Int) {
        searchBreeds.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(context)) {
                val response = breedRepository.searchBreeds(searchQuery, searchBreedPage)
                searchBreeds.postValue(handleSearchNewsResponse(response))
            } else
                searchBreeds.postValue(Resource.Error(context.getString(R.string.no_network_connection)))
        } catch (ex: Exception) {
            when (ex) {
                is IOException -> searchBreeds.postValue(Resource.Error(context.getString(R.string.network_failure)))
                else -> searchBreeds.postValue(Resource.Error(context.getString(R.string.error_in_data_conversion)))
            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<BreedResponse>): Resource<BreedResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchBreedsPage++
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}