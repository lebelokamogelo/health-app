package com.example.online_health_app.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [AvailableDoctorTable::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val availableDoctorDao: AvailableDoctorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "available_doctors_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}
