package dev.sakura.data.favourites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sakura.data.entities.FavouritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(favourite: FavouritesEntity)

    @Query("DELETE FROM favourites WHERE userId = :userId AND productId = :productId")
    suspend fun remove(userId: Long, productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE userId = :userId AND productId = :productId)")
    fun isFavourite(userId: Long, productId: String): Flow<Boolean>

    @Query("SELECT productId FROM favourites WHERE userId = :userId")
    fun getFavouriteProductIds(userId: Long): Flow<List<String>>

    @Query("SELECT productId FROM favourites WHERE userId IS NULL")
    fun getGuestFavouriteProductIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE userId IS NULL AND productId = :productId)")
    fun isGuestFavourite(productId: String): Flow<Boolean>

    @Query("DELETE FROM favourites WHERE userId IS NULL AND productId = :productId")
    suspend fun removeGuest(productId: String)

    @Transaction
    suspend fun assignGuestFavouritesToUser(userId: Long) {
        deleteGuestDuplicatesForUser(userId)
        updateRemainingGuestFavourites(userId)
    }

    @Query("""
        DELETE FROM favourites
        WHERE userId IS NULL
        AND productId IN (
        SELECT productId FROM favourites WHERE userId = :userId
        )
    """)
    suspend fun deleteGuestDuplicatesForUser(userId: Long)

    @Query("UPDATE favourites SET userId = :userId WHERE userId IS NULL")
    suspend fun updateRemainingGuestFavourites(userId: Long)
}
