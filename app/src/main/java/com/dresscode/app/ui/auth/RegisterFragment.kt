
package com.dresscode.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dresscode.app.databinding.FragmentRegisterBinding
import com.dresscode.app.data.model.Result

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()

        binding.goToLoginButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val nickname = binding.nicknameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
            
            if (username.isEmpty() || nickname.isEmpty() || password.isEmpty() || selectedGenderId == -1) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val gender = when(selectedGenderId) {
                binding.genderMale.id -> "male"
                binding.genderFemale.id -> "female"
                else -> "unknown"
            }
            
            viewModel.register(username, nickname, password, gender)
        }
    }
    
    private fun setupObservers() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingProgressBar.isVisible = true
                }
                is Result.Success -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Registration Successful! Please login.", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
                is Result.Error -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Registration Failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
