package dev.sakura.core.data

import dev.sakura.models.CartItem
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    val allCartItems: Flow<List<CartItem>>
    val cartTotalPrice: Flow<Double?>
    val cartTotalItemCount: Flow<Int?>

    suspend fun addItemToCart(product: ItemsModel, quantity: Int = 1)
    suspend fun updateItemQuantity(productId: String, newQuantity: Int)
    suspend fun removeItemFromCart(productId: String)
    suspend fun clearCart()
}
