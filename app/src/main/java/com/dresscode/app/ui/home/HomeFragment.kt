package com.dresscode.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dresscode.app.R
import com.dresscode.app.data.model.Result
import com.dresscode.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var postAdapter: OutfitPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        postAdapter = OutfitPostAdapter(
            onFavoriteClick = { post ->
                viewModel.toggleFavorite(post)
            },
            onItemClick = { post ->
                if (post.images.isNotEmpty()) {
                    val action = HomeFragmentDirections.actionNavigationHomeToPostDetailFragment(post.id)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, R.string.try_on_no_image, Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.postsRecyclerView.apply {
            adapter = postAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun setupObservers() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            binding.swipeRefreshLayout.isRefreshing = false
            postAdapter.submitList(posts)
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(context, getString(R.string.error_loading_posts, errorMessage), Toast.LENGTH_LONG).show()
        }
        
        viewModel.favoriteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* UI is updated optimistically */ }
                is Result.Success -> {
                    Toast.makeText(context, R.string.favorite_status_updated, Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    val message = getString(R.string.error_updating_favorite, result.exception.message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    // Revert UI change if needed by refreshing
                    viewModel.refreshPosts() 
                }
            }
        }
    }
    
    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
        }
    
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.applyFilter("q", query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.applyFilter("q", null)
                }
                return true
            }
        })
        
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            // A more complex implementation would handle multi-selection or dynamic filter values
            val filtersToClear = listOf("style", "season", "scene")
            filtersToClear.forEach { viewModel.applyFilter(it, null) }

            when (checkedId) {
                binding.chipStyle.id -> viewModel.applyFilter("style", "休闲")
                binding.chipSeason.id -> viewModel.applyFilter("season", "春季")
                binding.chipScene.id -> viewModel.applyFilter("scene", "日常")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
