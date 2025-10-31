package dev.sakura.core.auth

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManagerImpl @Inject constructor(@ApplicationContext context: Context) :
    SessionProvider {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ShopAppPrefs"
        private const val USER_ID_KEY = "user_id"
        private const val IS_LOGGED_IN_KEY = "is_logged_in"
        private const val AVATAR_URI_KEY_PREFIX = "avatar_uri_"
    }

    override fun createLoginSession(userId: Long) {
        val editor = prefs.edit()
        editor.putLong(USER_ID_KEY, userId)
        editor.putBoolean(IS_LOGGED_IN_KEY, true)
        editor.apply()
    }

    override fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN_KEY, false)
    }

    override fun getCurrentUserId(): Long? {
        val userId = prefs.getLong(USER_ID_KEY, -1L)
        return if (userId != -1L) userId else null
    }

    override fun logoutUser() {
        val editor = prefs.edit()
        editor.remove(USER_ID_KEY)
        editor.putBoolean(IS_LOGGED_IN_KEY, false)
        editor.apply()
    }

    fun saveAvatarForCurrentUser(avatarUri: String) {
        getCurrentUserId()?.let { userId ->
            prefs.edit().putString("$AVATAR_URI_KEY_PREFIX$userId", avatarUri).apply()
        }
    }

    fun getAvatarForCurrentUser(): String? {
        return getCurrentUserId()?.let { userId ->
            prefs.getString("$AVATAR_URI_KEY_PREFIX$userId", null)
        }
    }
}
