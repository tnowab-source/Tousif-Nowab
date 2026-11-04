package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "order_history")
data class OrderHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemsSummary: String,
    val encodedItems: String,
    val totalItemsCount: Int,
    val totalAmount: Int,
    val orderNote: String = "",
    val statusId: String = "preparing",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_items")
data class FavoriteItemEntity(
    @PrimaryKey val itemId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Dao
interface OrderHistoryDao {
    @Query("SELECT * FROM order_history ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderHistoryEntity)

    @Query("UPDATE order_history SET statusId = :statusId WHERE id = :id")
    suspend fun updateOrderStatus(id: Int, statusId: String)

    @Query("DELETE FROM order_history WHERE id = :id")
    suspend fun deleteOrderById(id: Int)

    @Query("DELETE FROM order_history")
    suspend fun clearAllOrders()
}

@Dao
interface FavoriteItemDao {
    @Query("SELECT itemId FROM favorite_items ORDER BY addedAt DESC")
    fun getAllFavoriteItemIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItemEntity)

    @Query("DELETE FROM favorite_items WHERE itemId = :itemId")
    suspend fun deleteFavoriteById(itemId: String)

    @Query("DELETE FROM favorite_items")
    suspend fun clearAllFavorites()
}

@Database(
    entities = [OrderHistoryEntity::class, FavoriteItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FoodStopDatabase : RoomDatabase() {
    abstract fun orderHistoryDao(): OrderHistoryDao
    abstract fun favoriteItemDao(): FavoriteItemDao

    companion object {
        @Volatile
        private var INSTANCE: FoodStopDatabase? = null

        fun getInstance(context: Context): FoodStopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodStopDatabase::class.java,
                    "food_stop_order_history.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class OrderHistoryRepository(
    private val dao: OrderHistoryDao,
    private val favoriteDao: FavoriteItemDao? = null
) {
    val allOrders: Flow<List<OrderHistoryEntity>> = dao.getAllOrders()
    val allFavoriteItemIds: Flow<List<String>>? = favoriteDao?.getAllFavoriteItemIds()

    suspend fun insert(order: OrderHistoryEntity) {
        dao.insertOrder(order)
    }

    suspend fun updateStatus(id: Int, statusId: String) {
        dao.updateOrderStatus(id, statusId)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteOrderById(id)
    }

    suspend fun clearAll() {
        dao.clearAllOrders()
    }

    suspend fun addFavorite(itemId: String) {
        favoriteDao?.insertFavorite(FavoriteItemEntity(itemId = itemId))
    }

    suspend fun removeFavorite(itemId: String) {
        favoriteDao?.deleteFavoriteById(itemId)
    }

    suspend fun clearAllFavorites() {
        favoriteDao?.clearAllFavorites()
    }
}
