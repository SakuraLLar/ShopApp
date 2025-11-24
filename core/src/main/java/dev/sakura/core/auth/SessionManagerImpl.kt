package dev.sakura.core.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManagerImpl @Inject constructor(@ApplicationContext context: Context) :
    SessionProvider {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _userIdFlow = MutableSharedFlow<Long?>(replay = 1)

    companion object {
        private const val PREFS_NAME = "ShopAppPrefs"
        private const val USER_ID_KEY = "user_id"
        private const val COVER_ID_KEY_PREFIX = "cover_id"
    }

    override fun createLoginSession(userId: Long) {
        prefs.edit().putLong(USER_ID_KEY, userId).apply()
        _userIdFlow.tryEmit(userId)
    }

    override fun isLoggedIn(): Boolean {
        return prefs.contains(USER_ID_KEY)
    }

    override fun getCurrentUserId(): Long? {
        val userId = prefs.getLong(USER_ID_KEY, -1L)
        return if (userId != -1L) userId else null
    }

    override fun logoutUser() {
        prefs.edit().remove(USER_ID_KEY).apply()
        _userIdFlow.tryEmit(null)
    }

    override fun getUserIdFlow(): Flow<Long?> {
        return _userIdFlow
            .onStart { emit(getCurrentUserId()) }
            .distinctUntilChanged()
    }

    override fun saveAvatarForCurrentUser(avatarUri: String) {
        getCurrentUserId()?.let { userId ->
            prefs.edit().putString("avatar_uri_$userId", avatarUri).apply()
        }
    }

    override fun getAvatarForCurrentUser(): String? {
        return getCurrentUserId()?.let { userId ->
            prefs.getString("avatar_uri_$userId", null)
        }
    }

    override fun saveCoverForCurrentUser(@DrawableRes coverId: Int) {
        getCurrentUserId()?.let { userId ->
            prefs.edit().putInt(COVER_ID_KEY_PREFIX + userId, coverId).apply()
        }
    }

    override fun getCoverForCurrentUser(): Int? {
        return getCurrentUserId()?.let { userId ->
            val coverId = prefs.getInt(COVER_ID_KEY_PREFIX + userId, -1)
            if (coverId != -1) coverId else null
        }
    }
}
