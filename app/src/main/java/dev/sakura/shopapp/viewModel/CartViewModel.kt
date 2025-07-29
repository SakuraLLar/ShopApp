package dev.sakura.shopapp.viewModel

import android.app.Application
import androidx.activity.result.launch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.sakura.shopapp.data.CartRepository
import dev.sakura.shopapp.db.AppDatabase
import dev.sakura.shopapp.db.CartItem
import dev.sakura.shopapp.model.ItemsModel
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CartRepository

    val allCartItems: LiveData<List<CartItem>>
    val cartTotalPrice: LiveData<Double?>
    val cartTotalItemCount: LiveData<Int?>

    init {
        val cartDao = AppDatabase.getDatabase(application).cartDao()
        repository = CartRepository(cartDao)
        allCartItems = repository.allCartItems.asLiveData()
        cartTotalPrice = repository.cartTotalPrice.asLiveData()
        cartTotalItemCount = repository.cartTotalItemCount.asLiveData()
    }

    fun addItemToCart(product: ItemsModel, quantity: Int = 1) = viewModelScope.launch {
        repository.addItemToCart(product, quantity)
    }

    fun updateItemQuantity(productId: String, newQuantity: Int) = viewModelScope.launch {
        repository.updateItemQuantity(productId, newQuantity)
    }

    fun removeItemFromCart(productId: String) = viewModelScope.launch {
        repository.removeItemFromCart(productId)
    }

    fun clearCart() = viewModelScope.launch {
        repository.clearCart()
    }
}
