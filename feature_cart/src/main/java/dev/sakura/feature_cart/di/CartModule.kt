package dev.sakura.feature_cart.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sakura.core.util.CartManager
import dev.sakura.feature_cart.domain.CartManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {
    @Binds
    @Singleton
    abstract fun bindCartManager(
        impl: CartManagerImpl,
    ): CartManager
}
