package dev.sakura.core.cart

import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow

interface CartManager {
    fun addItemToCart(item: ItemsModel)
    fun getCartItemsCount(): Flow<Int>
}
