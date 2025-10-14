package dev.sakura.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sakura.core.auth.SessionManagerImpl
import dev.sakura.core.auth.SessionProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {
    @Binds
    @Singleton
    abstract fun bindSessionProvider(impl: SessionManagerImpl): SessionProvider
}
