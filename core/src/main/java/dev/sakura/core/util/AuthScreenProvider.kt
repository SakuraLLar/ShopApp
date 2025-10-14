package dev.sakura.core.util

import androidx.fragment.app.FragmentManager

interface AuthScreenProvider {
    fun showLogin(fragmentManager: FragmentManager)
    fun showRegistration(fragmentManager: FragmentManager)
}
