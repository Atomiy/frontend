
package com.dresscode.app.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dresscode.app.R
import com.dresscode.app.data.model.Result
import com.dresscode.app.databinding.FragmentFavoritesBinding
import com.dresscode.app.ui.home.HomeFragmentDirections
import com.dresscode.app.ui.home.OutfitPostAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var postAdapter: OutfitPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        
        viewModel.fetchFavoritePosts()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        postAdapter = OutfitPostAdapter(
            onFavoriteClick = { /* Maybe refresh the list */ },
            onItemClick = { post ->
                // Navigate to post detail
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToPostDetailFragment(post.id)
                findNavController().navigate(action)
            }
        )
        binding.favoritesRecyclerView.apply {
            adapter = postAdapter
            // Using StaggeredGrid for a consistent look with the home page
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun setupObservers() {
        viewModel.favoritePosts.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.loadingProgressBar.isVisible = true
                is Result.Success -> {
                    binding.loadingProgressBar.isVisible = false
                    postAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Error: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
