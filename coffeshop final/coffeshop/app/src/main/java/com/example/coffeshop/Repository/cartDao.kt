package com.example.coffeshop.Repository

import androidx.room.*
import com.example.coffeshop.Domain.CartModel
import kotlinx.coroutines.flow.Flow

@Dao
interface cartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCart(cart: CartModel)

    @Query("SELECT * FROM cart_table")
    fun getAllCart(): Flow<List<CartModel>>

    @Query("DELETE FROM cart_table WHERE menu_id = :menuId")
    suspend fun deleteByMenuId(menuId: String)

    // PERBAIKAN: Menambahkan 'suspend' agar query ini benar-benar dijalankan di database
    @Query("UPDATE cart_table SET quantity = :newQty WHERE id = :id")
    suspend fun updateQuantity(id: Int, newQty: Int) // Pastikan id di sini Int

    @Query("DELETE FROM cart_table")
    suspend fun clearCart()

    @Query("DELETE FROM cart_table WHERE menu_id IN (:menuIds)")
    suspend fun deleteCheckoutedItems(menuIds: List<String>)
}