package com.example.riji.data.dao

import androidx.room.*
import com.example.riji.data.entity.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions ORDER BY startDate DESC")
    fun getAllSubscriptions(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE isActive = 1 ORDER BY startDate DESC")
    fun getActiveSubscriptions(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE isActive = 0 ORDER BY startDate DESC")
    fun getInactiveSubscriptions(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): Subscription?

    @Query("SELECT SUM(price) FROM subscriptions WHERE isActive = 1")
    fun getActiveSubscriptionTotal(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM subscriptions WHERE isActive = 1")
    fun getActiveSubscriptionCount(): Flow<Int>

    @Query("SELECT * FROM subscriptions WHERE name LIKE '%' || :query || '%'")
    fun searchSubscriptions(query: String): Flow<List<Subscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Delete
    suspend fun deleteSubscription(subscription: Subscription)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscriptionById(id: Long)
}
