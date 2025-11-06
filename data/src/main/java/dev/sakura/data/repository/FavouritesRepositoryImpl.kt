@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.sakura.data.repository

import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.data.FavouritesRepository
import dev.sakura.data.entities.FavouritesEntity
import dev.sakura.data.favourites.FavouritesDao
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesRepositoryImpl @Inject constructor(
    private val favouritesDao: FavouritesDao,
    private val sessionProvider: SessionProvider,
) : FavouritesRepository {

    override val favouriteProductIds: Flow<List<String>> =
        sessionProvider.getUserIdFlow().flatMapLatest { userId ->
            if (userId != null) {
                favouritesDao.getFavouriteProductIds(userId)
            } else {
                favouritesDao.getGuestFavouriteProductIds()
            }
        }

    override fun isFavourite(productId: String): Flow<Boolean> {
        return sessionProvider.getUserIdFlow().flatMapLatest { userId ->
            if (userId != null) {
                favouritesDao.isFavourite(userId, productId)
            } else {
                favouritesDao.isGuestFavourite(productId)
            }
        }
    }

    override suspend fun addFavourite(item: ItemsModel) {
        withContext(Dispatchers.IO) {
            val userId = sessionProvider.getCurrentUserId()
            val favourite =
                FavouritesEntity(userId = userId, productId = item.resourceId.toString())
            favouritesDao.add(favourite)
        }
    }

    override suspend fun removeFavourite(item: ItemsModel) {
        withContext(Dispatchers.IO) {
            val userId = sessionProvider.getCurrentUserId()
            val productId = item.resourceId.toString()
            if (userId != null) {
                favouritesDao.remove(userId, productId)
            } else {
                favouritesDao.removeGuest(productId)
            }
        }
    }

    suspend fun assignGuestFavouritesToUser(userId: Long) {
        withContext(Dispatchers.IO) {
            favouritesDao.assignGuestFavouritesToUser(userId)
        }
    }
}
