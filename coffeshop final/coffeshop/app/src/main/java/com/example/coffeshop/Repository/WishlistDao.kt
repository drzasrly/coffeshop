package com.example.coffeshop.Repository

import androidx.room.*
import com.example.coffeshop.Domain.WishlistModel
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWishlist(item: WishlistModel)

    @Delete
    suspend fun deleteWishlist(item: WishlistModel)

    @Query("SELECT * FROM wishlist_table")
    fun getAllWishlist(): Flow<List<WishlistModel>>

    // Tambahkan anotasi @Query di atas fungsi ini agar Room tahu apa yang harus dilakukan
    @Query("DELETE FROM wishlist_table WHERE menu_id = :mId")
    suspend fun deleteByMenuId(mId: String)
}