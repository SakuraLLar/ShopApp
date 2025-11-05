package dev.sakura.feature_cart.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.data.CartRepository
import dev.sakura.core.cart.CartManager
import dev.sakura.models.CartItemModel
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository,
    private val cartManager: CartManager,
) : ViewModel() {

    val allCartItems: LiveData<List<CartItemModel>> = repository.allCartItems.asLiveData()
    val cartTotalPrice: LiveData<Double?> = repository.cartTotalPrice.asLiveData()
    val cartItemCount: Flow<Int> = repository.cartTotalItemCount.map { it ?: 0 }


    fun addItemToCart(item: ItemsModel) {
        cartManager.addItemToCart(item)
    }

    fun updateItemQuantity(productId: String, newQuantity: Int) = viewModelScope.launch {
        repository.updateItemQuantity(productId, newQuantity)
    }

    fun removeItemFromCart(productId: String) = viewModelScope.launch {
        repository.removeItemFromCart(productId)
    }

    fun clearCartViewModelAction() = viewModelScope.launch {
        repository.clearCart()
    }
}
