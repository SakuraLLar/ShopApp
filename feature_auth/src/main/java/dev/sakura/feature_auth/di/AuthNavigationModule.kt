package dev.sakura.feature_auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sakura.core.util.AuthScreenProvider
import dev.sakura.feature_auth.navigation.AuthScreenProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthNavigationModule {
    @Binds
    @Singleton
    abstract fun bindAuthScreenProvider(
        impl: AuthScreenProviderImpl
    ): AuthScreenProvider
}
