package com.example.sampleusbproject.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sampleusbproject.MainActivity
import com.example.sampleusbproject.R
import com.example.sampleusbproject.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {
    
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            val phone = binding.editNumber.getRawPhoneNumber()
            val password = binding.etPassword.text.toString()
            
            if (validateInput(phone, password)) {
                viewModel.login(phone, password)
            }
        }
    }
    
    private fun validateInput(phone: String, password: String): Boolean {
        if (phone.isEmpty()) {
            binding.editNumber.showError = "Введите номер телефона"
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Введите пароль"
            return false
        }
        
        return true
    }
    
    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    (requireActivity() as? MainActivity)?.connectSocket()
                    findNavController().navigate(R.id.action_authFragment_to_mainFragment)
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 