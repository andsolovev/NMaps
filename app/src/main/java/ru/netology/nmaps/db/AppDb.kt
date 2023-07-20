package ru.netology.nmaps.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nmaps.dao.PlaceDao
import ru.netology.nmaps.entity.PlaceEntity

@Database(entities = [PlaceEntity::class], version = 1)
abstract class AppDb: RoomDatabase() {
    abstract val placeDao: PlaceDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context)
                    .also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .allowMainThreadQueries()
                .build()
    }
}