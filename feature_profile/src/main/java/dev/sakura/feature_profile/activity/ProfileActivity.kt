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
import dev.sakura.feature_auth.fragment.LoginDialogFragment
import dev.sakura.feature_auth.viewModel.AuthState.Loading
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

        setupToolbar()
        observeViewModel()
        updateUIBaseOnLoginStatus()
        setupThemeSwitch()

        binding.btnProfileAction.setOnClickListener {
            handleProfileAction()
        }

        initCustomBottomNavigation()
        (binding.includeBottomNavProfile as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_profile)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarProfile)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Профиль"
    }

    private fun observeViewModel() {
        authViewModel.currentUser.observe(this, Observer { user ->
            binding.progressBarProfile.visibility = View.GONE
            if (user != null && sessionManager.isLoggedIn()) {
                displayLoggedInUserUI(user)
            } else {
                displayGuestUI()
            }
        })

        authViewModel.authState.observe(this, Observer { state ->
            if (state is Loading) {
                binding.progressBarProfile.visibility = View.VISIBLE
                binding.btnProfileAction.isEnabled = false
            } else {
                binding.progressBarProfile.visibility = View.GONE
                binding.btnProfileAction.isEnabled = true
            }
        })
    }

    private fun updateUIBaseOnLoginStatus() {
        binding.progressBarProfile.visibility = View.VISIBLE
        if (sessionManager.isLoggedIn()) {
            sessionManager.getCurrentUserId()?.let { userId ->
                authViewModel.loadCurrentUserFromSession(userId)
            } ?: run {
                sessionManager.logoutUser()
                displayGuestUI()
                binding.progressBarProfile.visibility = View.GONE
            }
        } else {
            displayGuestUI()
            binding.progressBarProfile.visibility = View.GONE
        }
    }

    private fun displayLoggedInUserUI(user: User) {
        binding.txtProfileUserInfo.text = getString(R.string.profile_logged_in_as, user.firstName)
        binding.txtProfileUserEmail.text = user.email
        binding.txtProfileUserEmail.visibility = View.VISIBLE
        binding.btnProfileAction.text = getString(R.string.profile_action_logout)
    }

    private fun displayGuestUI() {
        binding.txtProfileUserInfo.text = getString(R.string.profile_guest)
        binding.txtProfileUserEmail.visibility = View.GONE
        binding.btnProfileAction.text = getString(R.string.profile_action_login)
    }

    private fun handleProfileAction() {
        if (sessionManager.isLoggedIn()) {
            sessionManager.logoutUser()
            updateUIBaseOnLoginStatus()
            recreate()
        } else {
            val loginDialog = LoginDialogFragment()
            loginDialog.show(supportFragmentManager, LoginDialogFragment.TAG)
        }
    }

    private fun setupThemeSwitch() {
        val currentTheme = ThemeManager.getSavedThemePreference(this)
        binding.switchTheme.isChecked = currentTheme == ThemeManager.THEME_DARK

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
