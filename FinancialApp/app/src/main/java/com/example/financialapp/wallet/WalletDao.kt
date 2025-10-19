package com.example.financialapp.wallet

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_cards ORDER BY id DESC")
    suspend fun getAll(): List<WalletEntity>

    @Query("SELECT * FROM wallet_cards WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WalletEntity?

    // For CardDetailScreen (live updates)
    @Query("SELECT * FROM wallet_cards WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<WalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: WalletEntity): Long

    @Update
    suspend fun update(card: WalletEntity)

    @Delete
    suspend fun delete(card: WalletEntity)

    // Optional, simpler delete path
    @Query("DELETE FROM wallet_cards WHERE id = :id")
    suspend fun deleteById(id: Long)
}