package com.example.online_health_app.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AvailableDoctorDao {

    @Query("SELECT * FROM available_doctors")
    fun getAllAvailableDoctors(): List<AvailableDoctorTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAvailableDoctor(availableDoctor: AvailableDoctorTable): Long

    @Query("DELETE FROM available_doctors")
    fun deleteAvailableDoctor()
}
