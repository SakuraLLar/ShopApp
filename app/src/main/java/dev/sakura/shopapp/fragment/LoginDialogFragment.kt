package dev.sakura.shopapp.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dev.sakura.shopapp.activity.MainActivity
import dev.sakura.shopapp.databinding.DialogLoginBinding
import dev.sakura.shopapp.util.SessionManager
import dev.sakura.shopapp.viewModel.AuthState
import dev.sakura.shopapp.viewModel.AuthViewModel

class LoginDialogFragment : DialogFragment() {
    private var _binding: DialogLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPasswordToggle()
        setupObservers()

        binding.btnLogin.setOnClickListener {
            val emailOrPhone = binding.etLoginEmailOrPhone.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (emailOrPhone.isEmpty()) {
                binding.tilLoginEmailOrPhone.error = "Это поле обязательно."
                Toast.makeText(context, "Введите Email или номер телефона.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                binding.tilLoginEmailOrPhone.error = null
            }

            if (password.isEmpty()) {
                binding.tilLoginPassword.error = "Это поле обязательно."
                Toast.makeText(context, "Введите пароль.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.tilLoginPassword.error = null
            }

            authViewModel.loginUser(emailOrPhone, password)
        }
    }

    private fun setupObservers() {
        authViewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            binding.btnLogin.isEnabled = state !is AuthState.Loading

            when (state) {
                is AuthState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    Toast.makeText(context, "Вход...", Toast.LENGTH_SHORT).show()
                }

                is AuthState.Success -> {
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }

                is AuthState.Error -> {
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }

                AuthState.Idle -> {
                    binding.btnLogin.isEnabled = true
                }
            }
        })

        authViewModel.navigationToMain.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { user ->
                sessionManager.createLoginSession(user.id)
                Toast.makeText(
                    context,
                    "Добро пожаловать, ${user.firstName}",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(
                    activity, MainActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
                dismiss()
                authViewModel.onLoginNavigationComplete()
            }
        })
    }

    private fun setupPasswordToggle() {
        binding.cbLoginShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etLoginPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etLoginPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
            binding.etLoginPassword.setSelection(binding.etLoginPassword.text?.length ?: 0)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LoginDialogFragment"
    }
}
