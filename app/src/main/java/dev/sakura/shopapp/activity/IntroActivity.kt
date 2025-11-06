package dev.sakura.shopapp.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.launch
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.SessionManagerImpl
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.util.AuthScreenProvider
import dev.sakura.shopapp.databinding.ActivityIntroBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var authScreenProvider: AuthScreenProvider

    @Inject
    lateinit var sessionProvider: SessionProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val userId = sessionProvider.getUserIdFlow().first()
            if (userId != null) {
                appNavigator.openMain(this@IntroActivity)
                finish()
            } else {
                setupIntroScreen()
            }
        }
    }

    private fun setupIntroScreen() {
        enableEdgeToEdge()
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            appNavigator.openMain(this)
        }

        binding.txtLogin.setOnClickListener {
            authScreenProvider.showLogin(supportFragmentManager)
        }

        binding.txtRegister.setOnClickListener {
            authScreenProvider.showRegistration(supportFragmentManager)
        }
    }
}
