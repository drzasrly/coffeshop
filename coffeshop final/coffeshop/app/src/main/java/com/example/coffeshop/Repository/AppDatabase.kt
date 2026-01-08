package com.example.coffeshop

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coffeshop.Domain.CartModel
import com.example.coffeshop.Domain.WishlistModel
import com.example.coffeshop.Repository.cartDao
import com.example.coffeshop.Repository.WishlistDao

// 1. Naikkan version ke 3 agar tabel cart_table dibuat ulang
@Database(entities = [WishlistModel::class, CartModel::class], version = 3, exportSchema = false) // NAIKKAN KE 3
abstract class AppDatabase : RoomDatabase() {

    abstract fun wishlistDao(): WishlistDao

    // Pastikan "cartDao" merujuk ke Interface DAO kamu
    abstract fun cartDao(): cartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffee_db"
                )
                    .fallbackToDestructiveMigration() // Ini akan menghapus data lama dan membuat tabel baru dengan kolom 'size'
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}