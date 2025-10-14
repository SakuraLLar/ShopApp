package dev.sakura.shopapp.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.util.AuthScreenProvider
import dev.sakura.shopapp.databinding.ActivityIntroBinding
import javax.inject.Inject

@AndroidEntryPoint
class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var authScreenProvider: AuthScreenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            appNavigator.openMain(this)
            finish()
        }

        binding.txtLogin.setOnClickListener {
            authScreenProvider.showLogin(supportFragmentManager)
        }

        binding.txtRegister.setOnClickListener {
            authScreenProvider.showRegistration(supportFragmentManager)
        }
    }
}
