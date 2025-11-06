package dev.sakura.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sakura.data.cart.CartDao
import dev.sakura.data.converters.Converters
import dev.sakura.data.converters.DateConverter
import dev.sakura.data.entities.CartItemEntity
import dev.sakura.data.entities.FavouritesEntity
import dev.sakura.data.entities.ItemsEntity
import dev.sakura.data.entities.OrderEntity
import dev.sakura.data.entities.OrderItemEntity
import dev.sakura.data.entities.UserEntity
import dev.sakura.data.favourites.FavouritesDao
import dev.sakura.data.orders.OrdersDao
import dev.sakura.data.product.ProductDao
import dev.sakura.data.user.UserDao

@Database(
    entities = [
        CartItemEntity::class,
        UserEntity::class,
        ItemsEntity::class,
        FavouritesEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun favouritesDao(): FavouritesDao
    abstract fun ordersDao(): OrdersDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shop_app_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
