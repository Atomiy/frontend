
package com.dresscode.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dresscode.app.R
import com.dresscode.app.data.local.SessionManager
import com.dresscode.app.databinding.FragmentLoginBinding
import com.dresscode.app.data.model.Result
import com.dresscode.app.ui.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()

        binding.goToRegisterButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.login_fragment_container, RegisterFragment())
                .addToBackStack(null) // Allows user to go back to login
                .commit()
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(username, password)
            } else {
                Toast.makeText(context, "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingProgressBar.isVisible = true
                }
                is Result.Success -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    
                    // Persist token
                    val sessionManager = SessionManager(requireContext())
                    sessionManager.saveAuthToken(result.data.token)
                    
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                is Result.Error -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Login Failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
