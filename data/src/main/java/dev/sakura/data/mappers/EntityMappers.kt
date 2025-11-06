package dev.sakura.data.mappers

import dev.sakura.data.entities.CartItemEntity
import dev.sakura.data.entities.ItemsEntity
import dev.sakura.data.entities.OrderWithItems
import dev.sakura.data.entities.UserEntity
import dev.sakura.models.CartItemModel
import dev.sakura.models.ItemsModel
import dev.sakura.models.OrderModel
import dev.sakura.models.UserModel

fun ItemsEntity.toModel() = ItemsModel(
    resourceId = resourceId,
    title = title,
    description = description,
    size = size,
    price = price,
    rating = rating,
    numberInCart = numberInCart,
    colorResourceNames = colorResourceNames
)

fun ItemsModel.toEntity() = ItemsEntity(
    resourceId = resourceId,
    title = title,
    description = description,
    size = size,
    price = price,
    rating = rating,
    numberInCart = numberInCart,
    colorResourceNames = colorResourceNames
)

fun UserEntity.toModel() = UserModel(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    passwordHash = passwordHash,
    gender = gender,
)

fun UserModel.toEntity() = UserEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    passwordHash = passwordHash,
    gender = gender,
)

fun CartItemEntity.toModel() = CartItemModel(
    productId = productId,
    title = title,
    price = price,
    imageResourceId = imageResourcedId,
    quantity = quantity
)

fun CartItemModel.toEntity(userId: Long?) = CartItemEntity(
    productId = productId,
    title = title,
    price = price,
    imageResourcedId = imageResourceId,
    quantity = quantity,
    userId = userId
)

fun OrderWithItems.toModel() = OrderModel(
    id = order.id,
    orderDate = order.orderDate,
    status = order.status,
    totalPrice = order.totalPrice,
    items = items.map { orderItem ->
        ItemsModel(
            resourceId = orderItem.imageResourceId ?: 0,
            title = orderItem.title,
            price = orderItem.price,
            numberInCart = orderItem.quantity,
            description = "",
            size = emptyList(),
            rating = 0.0,
            colorResourceNames = emptyList()
        )
    }
)
