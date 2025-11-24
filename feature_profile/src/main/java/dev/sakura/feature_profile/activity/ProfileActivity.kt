package dev.sakura.feature_profile.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.util.ThemeManager
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_profile.R
import dev.sakura.feature_profile.databinding.ActivityProfileBinding
import dev.sakura.feature_profile.util.GradientBorderProvider
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

    private val profileDetailsLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            authViewModel.currentUser.value?.let {
                displayLoggedInUserUI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupClickListeners()
        observeViewModels()
        setupThemeSwitch()
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

        binding.txtOpenProfileDetails.setOnClickListener {
            appNavigator.openProfileDetails(this, profileDetailsLauncher)
        }

        binding.imgNotificationIconProfile.setOnClickListener {
            appNavigator.openNotifications(supportFragmentManager)
        }
    }

    private fun observeViewModels() {
        authManager.currentUser.observe(this) { user ->
            if (user != null) {
                displayLoggedInUserUI(user)
            } else {
                displayGuestUI()
            }
        }
    }

    private fun displayLoggedInUserUI(user: UserModel) {
        binding.txtProfileUserInfo.text = getString(R.string.profile_logged_in_as, user.firstName)
        binding.txtOpenProfileDetails.visibility = View.VISIBLE

        val avatarUriString = sessionProvider.getAvatarForCurrentUser()
        if (avatarUriString != null) {
            Glide.with(this)
                .load(Uri.parse(avatarUriString))
                .placeholder(R.drawable.pic_avatar_placeholder)
                .error(R.drawable.pic_avatar_placeholder)
                .into(binding.imgProfileAvatar)
        } else {
            binding.imgProfileAvatar.setImageResource(R.drawable.pic_avatar_placeholder)
        }

        val currentCoverId =
            sessionProvider.getCoverForCurrentUser() ?: R.drawable.cover_gradient_lava
        val borderColors = GradientBorderProvider.coverToBorderColorMap[currentCoverId]
        val colorsToSet = borderColors
            ?: GradientBorderProvider.coverToBorderColorMap[R.drawable.cover_gradient_lava]!!
        binding.gradientBorderViewProfile.setGradientColors(
            colorsToSet.start,
            colorsToSet.center,
            colorsToSet.end
        )

        binding.btnLogin.visibility = View.GONE
        binding.btnRegister.visibility = View.GONE
        binding.btnLogout.visibility = View.VISIBLE
    }

    private fun displayGuestUI() {
        binding.txtProfileUserInfo.text = getString(R.string.profile_guest)
        binding.txtOpenProfileDetails.visibility = View.GONE
        binding.imgProfileAvatar.setImageResource(R.drawable.pic_avatar_placeholder)
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
