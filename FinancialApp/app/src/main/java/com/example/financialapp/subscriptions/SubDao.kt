package com.example.financialapp.subscriptions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sub: SubEntity)

    @Update
    suspend fun update(sub: SubEntity)

    @Delete
    suspend fun delete(sub: SubEntity)

    @Query("SELECT * FROM subscriptions ORDER BY dueDate ASC")
    fun readAllData(): LiveData<List<SubEntity>>

}