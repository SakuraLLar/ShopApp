package dev.sakura.feature_profile.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_profile.databinding.ActivityProfileDetailsBinding
import dev.sakura.models.UserModel
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class ProfileDetailsActivity : BaseActivity() {

    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var binding: ActivityProfileDetailsBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var isEditMode = false
    private var avatarUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.appbarProfileDetails) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        avatarUri = intent.getStringExtra(EXTRA_AVATAR_URI)

        setupClickListeners()
        observeViewModel()
        toggleEditMode(false)
        initCustomBottomNavigation()

        setupAppBarScrollAnimation()
    }

    private fun setupClickListeners() {
        binding.btnBackProfileDetails.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditProfileDetails.setOnClickListener {
            toggleEditMode(true)
        }

        binding.btnSaveProfileDetails.setOnClickListener {
            // TODO: Здесь будет логика сохранения данных из полей ввода
            toggleEditMode(false)
        }
    }

    private fun observeViewModel() {
        authViewModel.currentUser.observe(this) { user ->
            user?.let { displayUserData(it) }
        }
    }

    private fun displayUserData(user: UserModel) {
        binding.txtHeaderUserName.text = "${user.firstName} ${user.lastName ?: ""}"
        binding.inputEditFirstName.setText(user.firstName)
        binding.inputEditLastName.setText(user.lastName ?: "")
        binding.inputEditEmail.setText(user.email)
        binding.inputEditPhone.setText(user.phoneNumber)

        avatarUri?.let { uriString ->
            Glide.with(this)
                .load(Uri.parse(uriString))
                .placeholder(dev.sakura.common_ui.R.drawable.pic_avatar_placeholder)
                .into(binding.imageAvatarProfileDetails)
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        isEditMode = isEditing
        binding.inputEditFirstName.isEnabled = isEditing
        binding.inputEditLastName.isEnabled = isEditing
        binding.inputEditEmail.isEnabled = isEditing
        binding.inputEditPhone.isEnabled = isEditing

        binding.btnEditProfileDetails.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.btnSaveProfileDetails.visibility = if (isEditing) View.VISIBLE else View.GONE
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavProfileDetails

        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navCart.setOnClickListener { appNavigator.openCart(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }

    private fun setupAppBarScrollAnimation() {
        binding.appbarProfileDetails.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val totalScrollRange = appBarLayout.totalScrollRange
                val alpha = 1 - (abs(verticalOffset).toFloat() / totalScrollRange)
                binding.imageAvatarProfileDetails.alpha = alpha
                binding.txtHeaderUserName.alpha = alpha
                binding.btnBackProfileDetails.alpha = alpha
                binding.btnEditSaveContainer.alpha = alpha
            }
        )
    }

    companion object {
        const val EXTRA_AVATAR_URI = "extra_avatar_uri"
    }
}
