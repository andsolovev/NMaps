package ru.netology.nmaps.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmaps.dao.PlaceDao
import ru.netology.nmaps.db.dto.Place
import ru.netology.nmaps.entity.PlaceEntity

interface PlaceRepository {
    val data: Flow<List<Place>>
    fun save(place: Place)
    fun remove(id: Long)
    fun removeAll()
}

class PlaceRepositoryImpl(
    private val placeDao: PlaceDao
) : PlaceRepository {
    override val data: Flow<List<Place>> = placeDao.getAll()
        .map { it.map(PlaceEntity::toDto) }

    override fun save(place: Place) = placeDao.save(PlaceEntity.fromDto(place))

    override fun remove(id: Long) = placeDao.remove(id)

    override fun removeAll() = placeDao.removeAll()
}