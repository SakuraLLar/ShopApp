package dev.sakura.feature_catalog.repository

import dev.sakura.data.product.ProductDao
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mappers.toModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    private val productDao: ProductDao,
) {

    fun getPopulars(): Flow<List<ItemsModel>> {
        return productDao.getAllProducts().map { entityList ->
            entityList.map { entity -> entity.toModel() }
        }
    }

    fun findPopularById(id: Int): Flow<ItemsModel?> {
        return productDao.getProductById(id).map { entity ->
            entity?.toModel()
        }
    }
}
