
package com.dresscode.app.ui.postdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.dresscode.app.data.model.Post
import com.dresscode.app.data.model.Result
import com.dresscode.app.databinding.FragmentPostDetailBinding
import com.google.android.material.chip.Chip

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostDetailViewModel
    private val args: PostDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(PostDetailViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        viewModel.fetchPost(args.postId)
    }

    private fun setupObservers() {
        viewModel.post.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingProgressBar.isVisible = true
                }
                is Result.Success -> {
                    binding.loadingProgressBar.isVisible = false
                    bindPostData(result.data)
                }
                is Result.Error -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Error: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun bindPostData(post: Post) {
        binding.postTitle.text = post.title
        binding.postContent.text = post.content
        binding.authorNickname.text = post.author.nickname
        
        if (post.images.isNotEmpty()) {
            binding.postImage.load(post.images[0].url)
        }
        
        binding.authorAvatar.load(post.author.avatar) {
            placeholder(com.dresscode.app.R.drawable.ic_nav_settings)
        }
        
        binding.tagsChipGroup.removeAllViews()
        post.tags.forEach { tag ->
            val chip = Chip(context)
            chip.text = tag.name
            binding.tagsChipGroup.addView(chip)
        }
        
        binding.fabTryOn.setOnClickListener {
            if (post.images.isNotEmpty()) {
                val action = PostDetailFragmentDirections.actionPostDetailFragmentToNavigationTryOn(post.images[0].url)
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "No image to try on for this post.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
