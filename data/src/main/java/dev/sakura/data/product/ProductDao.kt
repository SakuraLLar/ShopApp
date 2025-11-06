package dev.sakura.data.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sakura.data.entities.ItemsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(products: List<ItemsEntity>)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ItemsEntity>>

    @Query("SELECT * FROM products WHERE resourceId = :id")
    fun getProductById(id: Int): Flow<ItemsEntity?>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Query("SELECT * FROM products WHERE resourceId IN (:ids)")
    fun findProductsByIds(ids: List<String>): Flow<List<ItemsEntity>>
}
