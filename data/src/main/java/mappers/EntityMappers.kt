package mappers

import dev.sakura.data.entities.CartItemEntity
import dev.sakura.data.entities.ItemsEntity
import dev.sakura.data.entities.UserEntity
import dev.sakura.models.CartItemModel
import dev.sakura.models.ItemsModel
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

fun CartItemModel.toEntity() = CartItemEntity(
    productId = productId,
    title = title,
    price = price,
    imageResourcedId = imageResourceId,
    quantity = quantity
)
