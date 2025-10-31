package dev.sakura.feature_profile.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.SessionManagerImpl
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.util.ThemeManager
import dev.sakura.feature_auth.viewModel.AuthState
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_profile.R
import dev.sakura.feature_profile.databinding.ActivityProfileBinding
import dev.sakura.models.User
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var binding: ActivityProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManagerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        sessionManager = SessionManagerImpl(this)

        observeViewModel()
        setupThemeSwitch()
        setupClickListeners()

        updateUIBaseOnLoginStatus()

        initCustomBottomNavigation()
        (binding.includeBottomNavProfile as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_profile)
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            appNavigator.openRegistration(supportFragmentManager)
        }
        binding.btnLogin.setOnClickListener {
            appNavigator.openLogin(supportFragmentManager)
        }
        binding.btnLogout.setOnClickListener {
            sessionManager.logoutUser()
            updateUIBaseOnLoginStatus()
            recreate()
        }
        binding.imageProfileAvatar.setOnClickListener {
            if (sessionManager.isLoggedIn()) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                Toast.makeText(this, "Войдите в аккаунт, чтобы измменить фото", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.txtOpenProfileDetails.setOnClickListener {
            val avatarUriString = sessionManager.getAvatarForCurrentUser()
            appNavigator.openProfileDetails(this, avatarUriString)
        }
    }

    private fun observeViewModel() {
        authViewModel.currentUser.observe(this, Observer { user ->
            if (user != null && sessionManager.isLoggedIn()) {
                displayLoggedInUserUI(user)
            } else {
                displayGuestUI()
            }
        })
        authViewModel.authState.observe(this, Observer { state ->
            if (state is AuthState.Loading) View.VISIBLE else View.GONE
        })
    }

    private fun updateUIBaseOnLoginStatus() {
        if (sessionManager.isLoggedIn()) {
            sessionManager.getCurrentUserId()?.let { userId ->
                authViewModel.loadCurrentUserFromSession(userId)
            } ?: run {
                sessionManager.logoutUser()
                displayGuestUI()
            }
        } else {
            displayGuestUI()
        }
    }

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent

            sessionManager.saveAvatarForCurrentUser(croppedUri.toString())
            Glide.with(this)
                .load(croppedUri)
                .into(binding.imageProfileAvatar)
        } else {
            val exception = result.error
            if (exception != null) {
                Toast.makeText(
                    this,
                    "Ошибка при обрезке фото: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                launchCropper(imageUri)
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(
                    this,
                    "Длы выбора фото нужно разрешение на доступ к хранилищу",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun launchCropper(uri: Uri) {
        val cropImageOptions = CropImageOptions(
            cropShape = CropImageView.CropShape.OVAL,
            fixAspectRatio = true,
            aspectRatioX = 1,
            aspectRatioY = 1,
            guidelines = CropImageView.Guidelines.ON_TOUCH,
            activityTitle = "Обрезать фото",
            cropMenuCropButtonTitle = "Готово"
        )
        val cropOptions = CropImageContractOptions(uri, cropImageOptions)
        cropImageLauncher.launch(cropOptions)
    }

    private fun displayLoggedInUserUI(user: User) {
        binding.txtProfileUserInfo.text = getString(R.string.profile_logged_in_as, user.firstName)
        binding.txtOpenProfileDetails.visibility = View.VISIBLE

        val avatarUriString = sessionManager.getAvatarForCurrentUser()
        if (avatarUriString != null) {
            Glide.with(this)
                .load(Uri.parse(avatarUriString))
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .into(binding.imageProfileAvatar)
        } else {
            binding.imageProfileAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
        }

        binding.btnLogin.visibility = View.GONE
        binding.btnRegister.visibility = View.GONE
        binding.btnLogout.visibility = View.VISIBLE
    }

    private fun displayGuestUI() {
        binding.txtProfileUserInfo.text = getString(R.string.profile_guest)
        binding.txtOpenProfileDetails.visibility = View.GONE
        binding.imageProfileAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
        binding.btnLogin.visibility = View.VISIBLE
        binding.btnRegister.visibility = View.VISIBLE
        binding.btnLogout.visibility = View.GONE
    }

    private fun setupThemeSwitch() {
        val isDarkMode = ThemeManager.getSavedThemePreference(this) == ThemeManager.THEME_DARK
        binding.switchTheme.isChecked = isDarkMode
        updateThemeIcon(isDarkMode)

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            val newTheme = if (isChecked) {
                ThemeManager.THEME_DARK
            } else {
                ThemeManager.THEME_LIGHT
            }
            ThemeManager.saveThemePreference(this, newTheme)
            recreate()
        }
    }

    private fun updateThemeIcon(isDark: Boolean) {
        if (isDark) {
            binding.iconTheme.setImageResource(R.drawable.ic_moon_mod)
        } else {
            binding.iconTheme.setImageResource(R.drawable.ic_sun_mod)
        }
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavProfile

        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navCart.setOnClickListener { appNavigator.openCart(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navOrders.setOnClickListener {
            Toast.makeText(this, "Orders Clicked (Not implemented)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUIBaseOnLoginStatus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
