package dev.sakura.feature_profile.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.auth.SessionManagerImpl
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.util.ThemeManager
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_profile.R
import dev.sakura.feature_profile.databinding.ActivityProfileBinding
import dev.sakura.models.UserModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var sessionProvider: SessionProvider

    private lateinit var binding: ActivityProfileBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        observeViewModel()
        setupThemeSwitch()
        setupClickListeners()

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
            sessionProvider.logoutUser()
            recreate()
        }
        binding.imageProfileAvatar.setOnClickListener {
            if (sessionProvider.isLoggedIn()) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                Toast.makeText(this, "Войдите в аккаунт, чтобы изменить фото", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.txtOpenProfileDetails.setOnClickListener {
            val avatarUriString = (sessionProvider as? SessionManagerImpl)?.getAvatarForCurrentUser()
            appNavigator.openProfileDetails(this, avatarUriString)
        }
    }

    private fun observeViewModel() {
        authManager.currentUser.observe(this) { user ->
            if (user != null) {
                displayLoggedInUserUI(user)
            } else {
                displayGuestUI()
            }
        }
    }

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            (sessionProvider as? SessionManagerImpl)?.saveAvatarForCurrentUser(croppedUri.toString())
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

    private fun displayLoggedInUserUI(user: UserModel) {
        binding.txtProfileUserInfo.text = getString(R.string.profile_logged_in_as, user.firstName)
        binding.txtOpenProfileDetails.visibility = View.VISIBLE

        val avatarUriString = (sessionProvider as? SessionManagerImpl)?.getAvatarForCurrentUser()
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
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
