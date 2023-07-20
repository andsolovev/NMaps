package ru.netology.nmaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmaps.db.dto.Place

@Entity
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toDto() =
        Place(
            id = id,
            title = title,
            description = description,
            latitude = latitude,
            longitude = longitude
        )
    companion object {
        fun fromDto(dto: Place) = PlaceEntity(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            latitude = dto.latitude,
            longitude = dto.longitude
        )
    }
}
