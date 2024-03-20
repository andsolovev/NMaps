package ru.netology.nmaps.dto

data class Place(
    val id: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)
