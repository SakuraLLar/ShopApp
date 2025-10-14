package dev.sakura.feature_favourites.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.feature_favourites.domain.FavouritesManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavouritesModule {
    @Binds
    @Singleton
    abstract fun bindFavouritesManager(impl: FavouritesManagerImpl): FavouritesManager
}
