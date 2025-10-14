package dev.sakura.feature_auth.navigation

import androidx.fragment.app.FragmentManager
import dev.sakura.core.util.AuthScreenProvider
import dev.sakura.feature_auth.fragment.LoginDialogFragment
import dev.sakura.feature_auth.fragment.RegisterBottomSheetDialogFragment
import javax.inject.Inject

class AuthScreenProviderImpl @Inject constructor() : AuthScreenProvider {
    override fun showLogin(fragmentManager: FragmentManager) {
        LoginDialogFragment().show(fragmentManager, LoginDialogFragment.TAG)
    }

    override fun showRegistration(fragmentManager: FragmentManager) {
        RegisterBottomSheetDialogFragment.newInstance()
            .show(fragmentManager, RegisterBottomSheetDialogFragment.TAG)
    }
}
