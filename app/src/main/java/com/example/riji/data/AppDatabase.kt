package com.example.riji.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.riji.data.dao.*
import com.example.riji.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Anniversary::class,
        CheckIn::class,
        CheckInRecord::class,
        Asset::class,
        Subscription::class,
        Plan::class,
        PlanTask::class,
        Diary::class,
        Category::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun anniversaryDao(): AnniversaryDao
    abstract fun checkInDao(): CheckInDao
    abstract fun assetDao(): AssetDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun planDao(): PlanDao
    abstract fun diaryDao(): DiaryDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 2 to 3: add manualProgress column to plans
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE plans ADD COLUMN manualProgress INTEGER NOT NULL DEFAULT -1")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "riji_database"
                )
                .addMigrations(MIGRATION_2_3)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert default categories using raw SQL to avoid deadlock
                        db.execSQL("INSERT INTO categories (name, sortOrder, isDefault) VALUES ('全部', 0, 1)")
                        db.execSQL("INSERT INTO categories (name, sortOrder, isDefault) VALUES ('我的生活', 1, 1)")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
