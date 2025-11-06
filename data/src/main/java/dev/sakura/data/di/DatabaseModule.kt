package dev.sakura.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sakura.data.AppDatabase
import dev.sakura.data.InitialProducts
import dev.sakura.data.cart.CartDao
import dev.sakura.data.favourites.FavouritesDao
import dev.sakura.data.mappers.toEntity
import dev.sakura.data.orders.OrdersDao
import dev.sakura.data.product.ProductDao
import dev.sakura.data.user.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDataBaseCallback(
        database: Provider<AppDatabase>,
    ): RoomDatabase.Callback {
        return object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val productDao = database.get().productDao()
                CoroutineScope(Dispatchers.IO).launch {
                    val initialPopularModels = InitialProducts.get()
                    val initialProductEntities = initialPopularModels.map { it.toEntity() }
                    productDao.insertAll(initialProductEntities)
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        callBack: RoomDatabase.Callback,
    ): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "shop_database"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(callBack)
            .build()
    }

    @Provides
    @Singleton
    fun provideCartDao(appDatabase: AppDatabase): CartDao {
        return appDatabase.cartDao()
    }

    @Provides
    @Singleton
    fun providerProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideFavouritesDao(appDatabase: AppDatabase): FavouritesDao {
        return appDatabase.favouritesDao()
    }

    @Provides
    @Singleton
    fun provideOrdersDao(appDatabase: AppDatabase): OrdersDao {
        return appDatabase.ordersDao()
    }
}
