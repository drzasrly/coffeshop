package com.example.coffeshop
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coffeshop.Domain.WishlistModel
import com.example.coffeshop.Repository.WishlistDao


@Database(entities = [WishlistModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffee_db" // Nama database bebas, 'coffee_db' lebih umum
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}