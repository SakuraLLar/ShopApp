package dev.sakura.data.repository

import dev.sakura.core.data.CartRepository
import dev.sakura.data.cart.CartDao
import dev.sakura.models.CartItem
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(private val cartDao: CartDao) : CartRepository {
    override val allCartItems: Flow<List<CartItem>> = cartDao.getAllItems()
    override val cartTotalPrice: Flow<Double?> = cartDao.getTotalPrice()
    override val cartTotalItemCount: Flow<Int?> = cartDao.getTotalItemCount()

    override suspend fun addItemToCart(product: ItemsModel, quantity: Int) {
        withContext(Dispatchers.IO) {
            val existingItem = cartDao.getItemById(product.resourceId.toString())
            if (existingItem != null) {
                existingItem.quantity += quantity
                cartDao.updateItem(existingItem)
            } else {
                val cartItem = CartItem(
                    productId = product.resourceId.toString(),
                    title = product.title,
                    price = product.price,
                    imageResourcedId = product.resourceId,
                    quantity = quantity
                )
                cartDao.insertItem(cartItem)
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
