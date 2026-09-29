package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        WishlistItemEntity::class,
        SavedBlueprintEntity::class,
        OrderEntity::class,
        WarrantyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WciDatabase : RoomDatabase() {
    abstract fun wciDao(): WciDao

    companion object {
        @Volatile
        private var INSTANCE: WciDatabase? = null

        fun getDatabase(context: Context): WciDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WciDatabase::class.java,
                    "wci_furniture_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
