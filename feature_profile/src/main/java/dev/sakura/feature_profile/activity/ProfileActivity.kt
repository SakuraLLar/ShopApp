package dev.sakura.feature_profile.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.SessionManagerImpl
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.util.ThemeManager
import dev.sakura.models.User
import dev.sakura.feature_auth.viewModel.AuthState
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_profile.R
import dev.sakura.feature_profile.databinding.ActivityProfileBinding
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

//        setupToolbar()
        observeViewModel()
        setupThemeSwitch()
//        updateThemeIcon()
        setupClickListeners()

        updateUIBaseOnLoginStatus()

        initCustomBottomNavigation()
        (binding.includeBottomNavProfile as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_profile)
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            appNavigator.openIntro(this) //openIntro
            finish()
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
            // TODO: Здесь будет логика для выбора фото
            Toast.makeText(this, "Выбор фото профиля (в разработке)", Toast.LENGTH_SHORT).show()
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

    private fun displayLoggedInUserUI(user: User) {
        binding.txtProfileUserInfo.text = getString(R.string.profile_logged_in_as, user.firstName)
        binding.txtProfileUserEmail.text = user.email
        binding.txtProfileUserEmail.visibility = View.VISIBLE

        // TODO: Загрузить фото пользователя с помощью Glide/Picasso
        // binding.imageProfileAvatar.load(user.avatarUrl)

        binding.btnLogin.visibility = View.GONE
        binding.btnRegister.visibility = View.GONE
        binding.btnLogout.visibility = View.VISIBLE
    }

    private fun displayGuestUI() {
        binding.txtProfileUserInfo.text = getString(R.string.profile_guest)
        binding.txtProfileUserEmail.visibility = View.GONE
        binding.imageProfileAvatar.setImageResource(R.drawable.ic_avatar_placeholder)

        binding.btnLogin.visibility = View.VISIBLE
        binding.btnRegister.visibility = View.VISIBLE
        binding.btnLogout.visibility = View.GONE
    }

    private fun setupThemeSwitch() {
        val isDarkMode = ThemeManager.getSavedThemePreference(this) == ThemeManager.THEME_DARK
        binding.switchTheme.isChecked = isDarkMode
        updateThemeIcon(isDarkMode)

        binding.switchTheme.setOnCheckedChangeListener { _,isChecked ->
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
