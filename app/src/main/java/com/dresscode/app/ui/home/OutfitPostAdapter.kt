
package com.dresscode.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dresscode.app.R
import com.dresscode.app.data.model.Post
import com.dresscode.app.databinding.ListItemOutfitBinding

class OutfitPostAdapter(
    private val onFavoriteClick: (Post) -> Unit,
    private val onItemClick: (Post) -> Unit
) : ListAdapter<Post, OutfitPostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ListItemOutfitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    inner class PostViewHolder(private val binding: ListItemOutfitBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }
        
        fun bind(post: Post) {
            binding.outfitTitle.text = post.title
            binding.favoriteButton.isChecked = post.isFavorited

            if (post.images.isNotEmpty()) {
                binding.outfitImage.load(post.images[0].url) {
                    placeholder(R.color.retro_light_gray)
                    error(R.color.retro_orange_light)
                }
            } else {
                binding.outfitImage.setImageResource(R.color.retro_light_gray)
            }
            
            binding.favoriteButton.setOnClickListener {
                // Instantly update UI to feel responsive, backend call will confirm
                val newPost = post.copy(isFavorited = !post.isFavorited)
                bind(newPost) // Re-bind with the new state
                onFavoriteClick(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
