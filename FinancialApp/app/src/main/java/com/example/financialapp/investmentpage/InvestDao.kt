package com.example.financialapp.investmentpage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestDao {
    @Query("SELECT * FROM investments ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<InvestEntity>>

    @Query("SELECT * FROM investments ORDER BY addedAt DESC")
    suspend fun getAll(): List<InvestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: InvestEntity): Long

    @Update
    suspend fun update(item: InvestEntity)

    @Delete
    suspend fun delete(item: InvestEntity)

    @Query("DELETE FROM investments")
    suspend fun clear()
}