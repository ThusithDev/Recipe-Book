package com.thusith.recipebook.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserEntity::class],
    version = 2,  // Incremented from previous version
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns
                database.execSQL("ALTER TABLE user ADD COLUMN email TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE user ADD COLUMN token TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE user ADD COLUMN lastLogin INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user ADD COLUMN image BLOB")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)  // Add migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// .fallbackToDestructiveMigration(false) // a temporary line