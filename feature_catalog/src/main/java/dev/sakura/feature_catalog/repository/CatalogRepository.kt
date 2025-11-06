package dev.sakura.feature_catalog.repository

import dev.sakura.data.mappers.toModel
import dev.sakura.data.product.ProductDao
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    fun getProductsByIds(ids: List<String>): Flow<List<ItemsModel>> {
        if (ids.isEmpty()) {
            return flowOf(emptyList())
        }
        return productDao.findProductsByIds(ids).map { entities ->
            entities.map { it.toModel() }
        }
    }
}
