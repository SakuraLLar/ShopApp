package dev.sakura.feature_cart.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.data.CartRepository
import dev.sakura.models.CartItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository,
) : ViewModel() {
    private val _selectionState = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val cartItems: StateFlow<List<CartItemModel>> = repository.allCartItems
        .combine(_selectionState) { items, selections ->
            items.map { item ->
                item.copy(isSelected = selections[item.productId] ?: true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPriceOfSelected: StateFlow<Double> = cartItems.map { items ->
        items.filter { it.isSelected }.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val isAllSelected: StateFlow<Boolean> = cartItems.map { items ->
        items.isNotEmpty() && items.all { it.isSelected }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleItemSelection(productId: String) {
        val newSelection = _selectionState.value.toMutableMap()
        val currentState = newSelection[productId] ?: true
        newSelection[productId] = !currentState
        _selectionState.value = newSelection
    }

    fun toggleSelectAll() {
        val areAllSelected = isAllSelected.value
        val newSelections = cartItems.value.associate { it.productId to !areAllSelected }
        _selectionState.value = newSelections
    }

    fun updateItemQuantity(productId: String, newQuantity: Int) = viewModelScope.launch {
        repository.updateItemQuantity(productId, newQuantity)
    }

    fun removeItemFromCart(productId: String) = viewModelScope.launch {
        repository.removeItemFromCart(productId)
    }
}
