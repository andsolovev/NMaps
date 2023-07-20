package ru.netology.nmaps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmaps.entity.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM PlaceEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(place: PlaceEntity)

    @Query("UPDATE PlaceEntity SET title = :title, description = :description WHERE id = :id")
    fun update(id: Long, title: String, description: String)

    @Query("DELETE FROM PlaceEntity WHERE id = :id")
    fun remove(id: Long)

    @Query("DELETE FROM PlaceEntity")
    fun removeAll()

    fun save(place: PlaceEntity) =
        if (place.id == 0L) add(place) else update(place.id, place.title, place.description)
}
