package dev.sakura.feature_profile.activity

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.auth.SessionProvider
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_profile.R
import dev.sakura.feature_profile.databinding.ActivitySettingsBinding
import dev.sakura.feature_profile.fragment.CoverSelectionBottomSheet
import dev.sakura.feature_profile.util.GradientBorderProvider
import dev.sakura.models.UserModel
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var isEditMode = false

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var sessionProvider: SessionProvider

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupClickListeners()
        setupEdgeToEdge()
        observeViewModels()
        toggleEditMode(false)
        loadAndDisplayCurrentCover()
    }

    private fun setupClickListeners() {
        binding.btnBackSettings.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnChangeCoverSettings.setOnClickListener {
            showCoverSelectionSheet()
        }
        binding.btnChangeAvatarSettings.setOnClickListener {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
        }
        binding.btnEditSaveSettings.setOnClickListener {
            if (isEditMode) {
                authViewModel.updateUser(
                    firstName = binding.inputEditFirstNameSettings.text.toString(),
                    lastName = binding.inputEditLastNameSettings.text.toString(),
                    email = binding.inputEditEmailSettings.text.toString(),
                    phoneNumber = binding.inputEditPhoneSettings.text.toString(),
                    newPassword = binding.inputEditPasswordSettings.text.toString()
                )
            } else {
                toggleEditMode(true)
            }
        }
    }

    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsCoordinatorLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.btnBackSettings.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 16
            }

            view.setPadding(insets.left, 0, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun observeViewModels() {
        authManager.currentUser.observe(this) { user ->
            if (!isEditMode) {
                user?.let { displayUserData(it) }
            }
        }

        authViewModel.updateState.observe(this) { event ->
            event.getContentIfNotHandled()?.let { result ->
                if (result.isSuccess) {
                    Toast.makeText(this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show()
                    toggleEditMode(false)
                    setResult(RESULT_OK)
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка: ${result.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun displayUserData(user: UserModel) {
        binding.inputEditFirstNameSettings.setText(user.firstName)
        binding.inputEditLastNameSettings.setText(user.lastName ?: "")
        binding.inputEditEmailSettings.setText(user.email)
        binding.inputEditPhoneSettings.setText(user.phoneNumber ?: "")

        val avatarUriString = sessionProvider.getAvatarForCurrentUser()
        val glideTarget = binding.imgAvatarSettings

        if (avatarUriString != null) {
            Glide.with(this)
                .load(Uri.parse(avatarUriString))
                .placeholder(R.drawable.pic_avatar_placeholder)
                .error(R.drawable.pic_avatar_placeholder)
                .into(glideTarget)
        } else {
            glideTarget.setImageResource(R.drawable.pic_avatar_placeholder)
        }
    }

    private fun showCoverSelectionSheet() {
        val currentCoverId =
            sessionProvider.getCoverForCurrentUser() ?: R.drawable.cover_gradient_lava
        val bottomSheet = CoverSelectionBottomSheet.newInstance(currentCoverId)

        bottomSheet.onSave = { selectedCover ->
            sessionProvider.saveCoverForCurrentUser(selectedCover.id)
            binding.imgCoverSettings.setImageResource(selectedCover.drawableRes)

            val borderColors = GradientBorderProvider.coverToBorderColorMap[selectedCover.id]
            if (borderColors != null) {
                binding.gradientBorderViewSettings.setGradientColors(
                    borderColors.start,
                    borderColors.center,
                    borderColors.end
                )
            }

            setResult(RESULT_OK)
            Toast.makeText(this, "Обложка сохранена", Toast.LENGTH_SHORT).show()
        }

        bottomSheet.show(supportFragmentManager, CoverSelectionBottomSheet.TAG)
    }

    private fun loadAndDisplayCurrentCover() {
        val currentCoverId =
            sessionProvider.getCoverForCurrentUser() ?: R.drawable.cover_gradient_lava
        binding.imgCoverSettings.setImageResource(currentCoverId)

        val borderColors = GradientBorderProvider.coverToBorderColorMap[currentCoverId]
        val colorsToSet = borderColors
            ?: GradientBorderProvider.coverToBorderColorMap[R.drawable.cover_gradient_lava]!!

        binding.gradientBorderViewSettings.setGradientColors(
            colorsToSet.start,
            colorsToSet.center,
            colorsToSet.end
        )
    }

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            sessionProvider.saveAvatarForCurrentUser(croppedUri.toString())
            Glide.with(this)
                .load(croppedUri)
                .into(binding.imgAvatarSettings)
            setResult(RESULT_OK)
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
                    "Для выбора фото нужно разрешение на доступ к хранилищу",
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

    private fun toggleEditMode(isEditing: Boolean) {
        isEditMode = isEditing

        binding.inputEditFirstNameSettings.isEnabled = isEditing
        binding.inputEditLastNameSettings.isEnabled = isEditing
        binding.inputEditEmailSettings.isEnabled = isEditing
        binding.inputEditPhoneSettings.isEnabled = isEditing
        binding.inputLayoutPasswordSettings.isEnabled = isEditing

        if (isEditing) {
            binding.btnEditSaveSettings.text = "Сохранить"
        } else {
            binding.btnEditSaveSettings.text = "Изменить"
        }
    }
}
