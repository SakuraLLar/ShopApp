package dev.sakura.feature_cart.domain

import dev.sakura.core.cart.CartManager
import dev.sakura.data.repository.CartRepositoryImpl
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManagerImpl @Inject constructor(
    private val cartRepository: CartRepositoryImpl,
) : CartManager {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun addItemToCart(item: ItemsModel) {
        scope.launch {
            cartRepository.addItemToCart(item)
        }
    }

    override fun getCartItemsCount(): Flow<Int> {
        return cartRepository.cartTotalItemCount.map { it ?: 0 }
    }
}
