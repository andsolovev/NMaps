package ru.netology.nmaps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.netology.nmaps.db.AppDb
import ru.netology.nmaps.db.dto.Place
import ru.netology.nmaps.repository.PlaceRepository
import ru.netology.nmaps.repository.PlaceRepositoryImpl

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlaceRepository = PlaceRepositoryImpl(AppDb.getInstance(application).placeDao)
    val data = repository.data
    fun save(place: Place) = repository.save(place)
    fun remove(id: Long) = repository.remove(id)
    fun removeAll() = repository.removeAll()
}