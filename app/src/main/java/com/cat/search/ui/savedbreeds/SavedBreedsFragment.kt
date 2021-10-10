package com.cat.search.ui.savedbreeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cat.search.R
import com.cat.search.adapter.BreedListAdapter
import com.cat.search.data.model.Breed
import com.cat.search.databinding.FragmentSavedBreedsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SavedBreedsFragment : Fragment(),
    BreedListAdapter.OnItemClickListener {

    private var _binding: FragmentSavedBreedsBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedBreedViewModel: SavedBreedViewModel
    private lateinit var breedListAdapter: BreedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBreedsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedBreedViewModel = ViewModelProvider(this)[SavedBreedViewModel::class.java]
        breedListAdapter = BreedListAdapter(this)

        binding.apply {
            rvSavedBreeds.apply {
                adapter = breedListAdapter
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val breed = breedListAdapter.currentList[viewHolder.adapterPosition]
                    savedBreedViewModel.onBreedSwiped(breed)
                }
            }).attachToRecyclerView(rvSavedBreeds)
        }

        getAllBreeds()
        observeSavedBreeds()
    }

    private fun getAllBreeds() {
        savedBreedViewModel.getAllBreeds().observe(viewLifecycleOwner) {
            breedListAdapter.submitList(it)
        }
    }

    private fun observeSavedBreeds() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            savedBreedViewModel.savedBreedEvent.collect { event ->
                when (event) {
                    is SavedBreedViewModel.SavedBreedEvent.ShowUndoDeleteBreedMessage -> {
                        Snackbar.make(
                            requireView(),
                            requireContext().getString(R.string.breed_deleted),
                            Snackbar.LENGTH_LONG
                        ).setAction("UNDO") {
                            savedBreedViewModel.onUndoClick(event.breed)
                        }.show()
                    }
                }
            }
        }
    }

    override fun onItemClick(breed: Breed) {
        val action = SavedBreedsFragmentDirections.actionSavedBreedsFragmentToBreedFragment(breed)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}