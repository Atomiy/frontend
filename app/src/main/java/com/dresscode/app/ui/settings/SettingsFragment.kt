
package com.dresscode.app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dresscode.app.data.local.SessionManager
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.model.User
import com.dresscode.app.databinding.FragmentSettingsBinding
import com.dresscode.app.ui.auth.LoginActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
        
        viewModel.fetchCurrentUser()
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val nickname = binding.nicknameEditText.text.toString().trim()
            val gender = when(binding.genderRadioGroup.checkedRadioButtonId) {
                binding.genderMale.id -> "male"
                binding.genderFemale.id -> "female"
                else -> "all" // Or "unknown", depending on desired backend value
            }
            viewModel.updateCurrentUser(nickname, gender)
        }
        
        binding.logoutButton.setOnClickListener {
            sessionManager.clearAuthToken()
            val intent = Intent(activity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Loading -> binding.loadingProgressBar.isVisible = true
                is Result.Success -> {
                    binding.loadingProgressBar.isVisible = false
                    populateUserData(result.data)
                }
                is Result.Error -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Error fetching user data: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
             when(result) {
                is Result.Loading -> binding.loadingProgressBar.isVisible = true
                is Result.Success -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    populateUserData(result.data)
                }
                is Result.Error -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Update failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun populateUserData(user: User) {
        binding.nicknameEditText.setText(user.nickname)
        binding.usernameText.setText(user.username)
        when(user.gender) {
            "male" -> binding.genderMale.isChecked = true
            "female" -> binding.genderFemale.isChecked = true
            else -> binding.genderAll.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
