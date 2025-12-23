
package com.dresscode.app.ui.createpost

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.dresscode.app.R
import com.dresscode.app.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreatePostViewModel

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            bytes?.let { data ->
                binding.postImagePreview.load(data)
                viewModel.uploadPostImage(data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(CreatePostViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.postImagePreview.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.publishButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val content = binding.contentEditText.text.toString().trim()
            val style = binding.styleEditText.text.toString().trim()
            val season = binding.seasonEditText.text.toString().trim()
            val scene = binding.sceneEditText.text.toString().trim()
            val tags = binding.tagsEditText.text.toString().split(",").map { it.trim() }

            if (title.isEmpty() || content.isEmpty() || style.isEmpty() || season.isEmpty() || scene.isEmpty()) {
                Toast.makeText(context, R.string.register_fill_all_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            viewModel.createPost(title, content, style, season, scene, tags)
        }
    }

    private fun setupObservers() {
        viewModel.createState.observe(viewLifecycleOwner) { state ->
            binding.loadingProgressBar.isVisible = state is CreatePostViewModel.CreatePostState.ImageUploading || state is CreatePostViewModel.CreatePostState.PostPublishing

            when (state) {
                is CreatePostViewModel.CreatePostState.Success -> {
                    Toast.makeText(context, R.string.post_publish_success, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Go back to the previous screen
                }
                is CreatePostViewModel.CreatePostState.Error -> {
                    val message = getString(R.string.error_with_message, state.message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Idle, Uploading, Publishing
                }
            }
        }
        
        viewModel.getUploadedImageUrl()?.let {
             binding.postImagePreview.load(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
