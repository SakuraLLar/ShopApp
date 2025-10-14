package dev.sakura.feature_auth.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.models.User
import dev.sakura.feature_auth.databinding.BottomSheetRegisterBinding
import dev.sakura.core.auth.SessionManagerImpl
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_auth.viewModel.AuthState
import dev.sakura.feature_auth.viewModel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RegisterBottomSheetDialogFragment : BottomSheetDialogFragment() {
    @Inject
    lateinit var appNavigator: AppNavigator

    private var _binding: BottomSheetRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManagerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManagerImpl(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPasswordToggle()
        setupObservers()

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setupObservers() {
        authViewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            val isLoading = state is AuthState.Loading
            binding.btnRegister.isEnabled = !isLoading

            when (state) {
                is AuthState.Loading -> {
                    binding.btnRegister.isEnabled = false
                    Toast.makeText(context, "Регистрация...", Toast.LENGTH_SHORT).show()
                }

                is AuthState.Success,
                    -> {
//                    binding.btnRegister.isEnabled = true
//                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }

                is AuthState.Error -> {
//                    binding.btnRegister.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }

                AuthState.Idle -> {
                    binding.btnRegister.isEnabled = true
                }
            }
        })

        authViewModel.registrationSuccessAction.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { registeredUser: User ->
                Toast.makeText(
                    context,
                    "Регистрация успешна! Добро пожаловать, ${registeredUser.firstName}!",
                    Toast.LENGTH_LONG
                ).show()
                sessionManager.createLoginSession(registeredUser.id)
                appNavigator.openMain(requireActivity())
                requireActivity().finishAffinity()
                dismiss()
                authViewModel.onRegistrationNavigationComplete()
            }
        })
    }

    private fun setupPasswordToggle() {
        binding.cbRegisterShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etRegisterPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etRegisterPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
            binding.etRegisterPassword.setSelection(binding.etRegisterPassword.text?.length ?: 0)
        }
    }

    private fun registerUser() {
        val firstName = binding.etRegisterFirstName.text.toString().trim()
        val lastName = binding.etRegisterLastName.text.toString().trim().ifEmpty { null }
        val email = binding.etRegisterEmailName.text.toString().trim()
        val phoneNumber = binding.etRegisterPhoneNumber.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()

        val selectedGenderId = binding.rgRegisterGroupGender.checkedRadioButtonId
        val gender: String? = if (selectedGenderId != -1) {
            view?.findViewById<RadioButton>(selectedGenderId)?.text?.toString()
        } else {
            null
        }

        if (!validateInputs(firstName, email, phoneNumber, password)) {
            return
        }

        authViewModel.registerUser(firstName, lastName, email, phoneNumber, password, gender)
    }

    private fun validateInputs(
        firstName: String,
        email: String,
        phoneNumber: String,
        password: String,
    ): Boolean {
        var isValid = true
        binding.tilRegisterFirstName.error = null
        binding.tilRegisterEmail.error = null
        binding.tilRegisterPhoneNumber.error = null
        binding.tilRegisterPassword.error = null

        if (firstName.isEmpty()) {
            binding.tilRegisterFirstName.error = "Имя - обязательно"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tilRegisterEmail.error = "Почта - обязательно"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilRegisterEmail.error = "Некорректный формат почты"
            isValid = false
        }

        if (phoneNumber.isEmpty()) {
            binding.tilRegisterPhoneNumber.error = "Номер телефона - обязательно"
            isValid = false
        } // Дополнительные проверки телефона

        if (password.isEmpty()) {
            binding.tilRegisterPassword.error = "Пароль - обязательно"
            isValid = false
        } else if (password.length < 6) {
            binding.tilRegisterPassword.error = "Пароль должен быть не менее 6 символов"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(context, "Пожалуйста, исправьте ошибки в форме", Toast.LENGTH_SHORT)
                .show()
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "RegisterBottomSheet"
        fun newInstance(): RegisterBottomSheetDialogFragment {
            return RegisterBottomSheetDialogFragment()
        }
    }
}
