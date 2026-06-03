package com.example.riji.data.dao

import androidx.room.*
import com.example.riji.data.entity.Asset
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets ORDER BY purchaseDate DESC")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE category = :category ORDER BY purchaseDate DESC")
    fun getAssetsByCategory(category: String): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Long): Asset?

    @Query("SELECT SUM(price) FROM assets")
    fun getTotalAssetValue(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM assets")
    fun getAssetCount(): Flow<Int>

    @Query("SELECT * FROM assets WHERE name LIKE '%' || :query || '%'")
    fun searchAssets(query: String): Flow<List<Asset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset): Long

    @Update
    suspend fun updateAsset(asset: Asset)

    @Delete
    suspend fun deleteAsset(asset: Asset)

    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun deleteAssetById(id: Long)
}
