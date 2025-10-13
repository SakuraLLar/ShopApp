package dev.sakura.data.repository

import dev.sakura.data.cart.CartDao
import dev.sakura.models.CartItem
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(private val cartDao: CartDao) {
    val allCartItems: Flow<List<CartItem>> = cartDao.getAllItems()
    val cartTotalPrice: Flow<Double?> = cartDao.getTotalPrice()
    val cartTotalItemCount: Flow<Int?> = cartDao.getTotalItemCount()

    suspend fun addItemToCart(product: ItemsModel, quantity: Int = 1) {
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

    suspend fun updateItemQuantity(productId: String, newQuantity: Int) {
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

    suspend fun removeItemFromCart(productId: String) {
        withContext(Dispatchers.IO) {
            cartDao.deleteItemById(productId)
        }
    }

    suspend fun clearCart() {
        withContext(Dispatchers.IO) {
            cartDao.clearCart()
        }
    }
}
