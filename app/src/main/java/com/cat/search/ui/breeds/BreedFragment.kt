package com.cat.search.ui.breeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.cat.search.R
import com.cat.search.databinding.FragmentBreedBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BreedFragment : Fragment() {

    private var _binding: FragmentBreedBinding? = null
    private val binding get() = _binding!!
    private lateinit var breedViewModel: BreedViewModel
    private val args by navArgs<BreedFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        breedViewModel = ViewModelProvider(this)[BreedViewModel::class.java]

        binding.apply {
            val breed = args.breed
            webView.apply {
                webViewClient = WebViewClient()
                breed.wikipedia_url?.let {
                    loadUrl(breed.wikipedia_url)
                }
            }

            favButton.setOnClickListener {
                if (breed.image != null) {
                    breedViewModel.saveBreed(breed)
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.problem_in_data_saving),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        observeBreedData()
    }

    private fun observeBreedData() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            breedViewModel.breedEvent.collect { event ->
                when (event) {
                    is BreedViewModel.BreedEvent.ShowBreedSavedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}