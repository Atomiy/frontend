
package com.dresscode.app.ui.tryon

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
import androidx.navigation.fragment.navArgs
import coil.load
import com.dresscode.app.databinding.FragmentTryOnBinding
import java.io.ByteArrayOutputStream

class TryOnFragment : Fragment() {

    private var _binding: FragmentTryOnBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TryOnViewModel
    private val args: TryOnFragmentArgs by navArgs()

    private val personImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleImageSelection(it, "person") }
    }
    
    private val clothingImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleImageSelection(it, "clothing") }
    }
    
    private fun handleImageSelection(uri: Uri, type: String) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let { viewModel.uploadImage(type, it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTryOnBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(TryOnViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupInitialState()
        setupClickListeners()
        setupObservers()
    }
    
    private fun setupInitialState() {
        viewModel.setInitialClothingImage(args.clothingImageUrl)
    }

    private fun setupClickListeners() {
        binding.uploadButton.setOnClickListener {
            personImageLauncher.launch("image/*")
        }
        
        binding.personCard.setOnClickListener {
             personImageLauncher.launch("image/*")
        }
        
        binding.clothingCard.setOnClickListener {
            clothingImageLauncher.launch("image/*")
        }

        binding.tryOnButton.setOnClickListener {
            viewModel.startTryOnProcess()
        }
    }

    private fun setupObservers() {
        val ensureScheme = { url: String ->
            if (url.startsWith("http://") || url.startsWith("https://")) url else "http://$url"
        }

        viewModel.personImageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { binding.personImageView.load(ensureScheme(it)) }
        }
        
        viewModel.clothingImageUrl.observe(viewLifecycleOwner) { url ->
            url?.let { binding.clothingImageView.load(ensureScheme(it)) }
        }

        viewModel.tryOnState.observe(viewLifecycleOwner) { state ->
            binding.loadingProgressBar.isVisible = state is TryOnViewModel.TryOnState.Uploading || state is TryOnViewModel.TryOnState.Processing
            binding.resultPlaceholder.isVisible = state !is TryOnViewModel.TryOnState.Success

            when (state) {
                is TryOnViewModel.TryOnState.Uploading -> {
                    binding.resultPlaceholder.text = "Uploading image..."
                }
                is TryOnViewModel.TryOnState.Processing -> {
                    binding.resultPlaceholder.text = "AI is working its magic..."
                }
                is TryOnViewModel.TryOnState.Success -> {
                    binding.resultImageView.load(state.resultUrl)
                }
                is TryOnViewModel.TryOnState.Error -> {
                    Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    binding.resultPlaceholder.text = "An error occurred."
                }
                is TryOnViewModel.TryOnState.Idle -> {
                     binding.resultPlaceholder.text = "Result will appear here"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
