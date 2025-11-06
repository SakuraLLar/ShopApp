package dev.sakura.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sakura.core.auth.SessionManagerImpl
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.data.CartRepository
import dev.sakura.core.data.FavouritesRepository
import dev.sakura.core.data.OrdersRepository
import dev.sakura.core.data.UserRepository
import dev.sakura.data.repository.CartRepositoryImpl
import dev.sakura.data.repository.FavouritesRepositoryImpl
import dev.sakura.data.repository.OrdersRepositoryImpl
import dev.sakura.data.repository.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSessionProvider(impl: SessionManagerImpl): SessionProvider

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindFavouritesRepository(impl: FavouritesRepositoryImpl): FavouritesRepository

    @Binds
    @Singleton
    abstract fun bindOrdersRepository(impl: OrdersRepositoryImpl): OrdersRepository
}
