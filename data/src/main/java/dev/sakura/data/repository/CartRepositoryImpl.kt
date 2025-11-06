@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package dev.sakura.data.repository

import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.data.CartRepository
import dev.sakura.data.cart.CartDao
import dev.sakura.data.mappers.toEntity
import dev.sakura.data.mappers.toModel
import dev.sakura.models.CartItemModel
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val sessionProvider: SessionProvider,
) : CartRepository {

    override val allCartItems: Flow<List<CartItemModel>> =
        sessionProvider.getUserIdFlow().flatMapLatest { userId ->
            val itemsFlow = if (userId != null) {
                cartDao.getAllItems(userId)
            } else {
                cartDao.getGuestAllItems()
            }
            itemsFlow.map { entityList -> entityList.map { it.toModel() } }
        }

    override val cartTotalPrice: Flow<Double?> =
        sessionProvider.getUserIdFlow().flatMapLatest { userId ->
            if (userId != null) {
                cartDao.getTotalPrice(userId)
            } else {
                cartDao.getGuestTotalPrice()
            }
        }

    override val cartTotalItemCount: Flow<Int?> =
        sessionProvider.getUserIdFlow().flatMapLatest { userId ->
            if (userId != null) {
                cartDao.getTotalItemCount(userId)
            } else {
                cartDao.getGuestTotalItemCount()
            }
        }

    override suspend fun addItemToCart(product: ItemsModel, quantity: Int) {
        withContext(Dispatchers.IO) {
            val userId = sessionProvider.getCurrentUserId()
            val cartItemModel = CartItemModel(
                productId = product.resourceId.toString(),
                title = product.title,
                price = product.price,
                imageResourceId = product.resourceId,
                quantity = quantity
            )

            val existingItemEntity = if (userId != null) {
                cartDao.getItemById(cartItemModel.productId, userId)
            } else {
                cartDao.getGuestItemById(cartItemModel.productId)
            }

            if (existingItemEntity != null) {
                existingItemEntity.quantity += quantity
                cartDao.updateItem(existingItemEntity)
            } else {
                cartDao.insertItem(cartItemModel.toEntity(userId))
            }
        }
    }

    override suspend fun updateItemQuantity(productId: String, newQuantity: Int) {
        withContext(Dispatchers.IO) {
            val userId = sessionProvider.getCurrentUserId()
            val item = if (userId != null) {
                cartDao.getItemById(productId, userId)
            } else {
                cartDao.getGuestItemById(productId)
            }
            if (item != null) {
                if (newQuantity > 0) {
                    item.quantity = newQuantity
                    cartDao.updateItem(item)
                } else {
                    removeItemFromCart(productId)
                }
            }
        }
    }

    override suspend fun removeItemFromCart(productId: String) {
        withContext(Dispatchers.IO) {
            val userId = sessionProvider.getCurrentUserId()
            if (userId != null) {
                cartDao.deleteItemById(productId, userId)
            } else {
                cartDao.deleteGuestItemById(productId)
            }
        }
    }

    override suspend fun clearCart() {
        withContext(Dispatchers.IO) {
            val userId = sessionProvider.getCurrentUserId()
            if (userId != null) {
                cartDao.clearCart(userId)
            } else {
                cartDao.clearGuestCart()
            }
        }
    }

    suspend fun assignGuestCartToUser(userId: Long) {
        withContext(Dispatchers.IO) {
            cartDao.assignGuestCartToUser(userId)
        }
    }
}
