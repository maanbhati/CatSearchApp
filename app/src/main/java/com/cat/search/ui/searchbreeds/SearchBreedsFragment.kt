package com.cat.search.ui.searchbreeds

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cat.search.adapter.BreedListAdapter
import com.cat.search.data.model.Breed
import com.cat.search.databinding.FragmentSearchBreedsBinding
import com.cat.search.util.QUERY_PAGE_SIZE
import com.cat.search.util.Resource
import com.cat.search.util.SEARCH_BREEDS_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchBreedsFragment : Fragment(), BreedListAdapter.OnItemClickListener {

    private var _binding: FragmentSearchBreedsBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchBreedViewModel: SearchBreedsViewModel
    private lateinit var breedListAdapter: BreedListAdapter
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBreedsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchBreedViewModel = ViewModelProvider(this)[SearchBreedsViewModel::class.java]
        breedListAdapter = BreedListAdapter(this)

        binding.apply {
            rvSearchBreeds.apply {
                adapter = breedListAdapter
                setHasFixedSize(true)
                addOnScrollListener(this@SearchBreedsFragment.scrollListener)
            }
        }

        var job: Job? = null
        binding.editSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_BREEDS_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        searchBreedViewModel.searchBreeds(editable.toString())
                    }
                }
            }
        }

        observeSearchedBreeds()
    }

    private fun observeSearchedBreeds() {
        searchBreedViewModel.searchBreeds.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    isLoading = false
                    binding.progressBar.visibility = View.INVISIBLE
                    it.data?.let { newsResponse ->
                        breedListAdapter.submitList(newsResponse)
                        val totalPages = newsResponse.size / QUERY_PAGE_SIZE + 2
                        isLastPage = searchBreedViewModel.searchBreedsPage == totalPages
                        if (isLastPage)
                            binding.rvSearchBreeds.setPadding(0, 0, 0, 0)
                    }
                }
                is Resource.Error -> {
                    isLoading = true
                    binding.progressBar.visibility = View.INVISIBLE
                    it.message?.let { message ->
                        Log.e("SearchBreedsFragment", message)
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //State is scrolling
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                searchBreedViewModel.searchBreeds(binding.editSearch.text.toString())
                isScrolling = false
            }
        }
    }

    override fun onItemClick(breed: Breed) {
        val action =
            SearchBreedsFragmentDirections.actionSearchBreedsFragmentToBreedFragment(breed)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}