package dev.sakura.shopapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sakura.core.orders.OrdersManager
import dev.sakura.core.orders.OrdersManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindOrderManager(impl: OrdersManagerImpl): OrdersManager
}
