package dev.sakura.data.repository

import dev.sakura.core.data.CartRepository
import dev.sakura.data.cart.CartDao
import dev.sakura.data.entities.CartItemEntity
import dev.sakura.models.CartItemModel
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mappers.toEntity
import mappers.toModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(private val cartDao: CartDao) : CartRepository {
    override val allCartItems: Flow<List<CartItemModel>> =
        cartDao.getAllItems().map { entityList ->
            entityList.map { entity ->
                entity.toModel()
            }
        }
    override val cartTotalPrice: Flow<Double?> = cartDao.getTotalPrice()
    override val cartTotalItemCount: Flow<Int?> = cartDao.getTotalItemCount()

    override suspend fun addItemToCart(product: ItemsModel, quantity: Int) {
        withContext(Dispatchers.IO) {
            val cartItemModel = CartItemModel(
                productId = product.resourceId.toString(),
                title = product.title,
                price = product.price,
                imageResourceId = product.resourceId,
                quantity = quantity
            )

            val existingItemEntity = cartDao.getItemById(cartItemModel.productId)

            if (existingItemEntity != null) {
                existingItemEntity.quantity += quantity
                cartDao.updateItem(existingItemEntity)
            } else {
                cartDao.insertItem(cartItemModel.toEntity())
            }
        }
    }

    override suspend fun updateItemQuantity(productId: String, newQuantity: Int) {
        withContext(Dispatchers.IO) {
            val item = cartDao.getItemById(productId)
            if (item != null) {
                if (newQuantity > 0) {
                    item.quantity = newQuantity
                    cartDao.updateItem(item)
                } else {
                    cartDao.deleteItemById(productId)
                }
            }
        }
    }

    override suspend fun removeItemFromCart(productId: String) {
        withContext(Dispatchers.IO) {
            cartDao.deleteItemById(productId)
        }
    }

    override suspend fun clearCart() {
        withContext(Dispatchers.IO) {
            cartDao.clearCart()
        }
    }
}
