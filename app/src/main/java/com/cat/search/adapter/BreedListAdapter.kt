package com.cat.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cat.search.data.model.Breed
import com.cat.search.databinding.ItemBreedListBinding
import com.cat.search.util.loadImage
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class BreedListAdapter @Inject constructor(private val listener: OnItemClickListener) :
    ListAdapter<Breed, BreedListAdapter.BreedsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedsViewHolder {
        val binding =
            ItemBreedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BreedsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreedsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class BreedsViewHolder(private val binding: ItemBreedListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val caseStudy = getItem(position)
                        listener.onItemClick(caseStudy)
                    }
                }
            }
        }

        fun bind(breed: Breed) {
            binding.apply {
                catImage.loadImage(breed.image?.url)
                catName.text = breed.name.plus(", Energy level- ${breed.energy_level}")
                temperament.text = breed.temperament
                wikiUrl.text = breed.wikipedia_url
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(breed: Breed)
    }

    class DiffCallback : DiffUtil.ItemCallback<Breed>() {
        override fun areItemsTheSame(
            oldItem: Breed,
            newItem: Breed
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Breed,
            newItem: Breed
        ): Boolean {
            return oldItem == newItem
        }
    }
}