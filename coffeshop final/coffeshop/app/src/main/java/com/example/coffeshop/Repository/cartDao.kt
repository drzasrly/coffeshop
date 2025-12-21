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

    @Query("UPDATE cart_table SET quantity = :qty WHERE menu_id = :menuId")
    suspend fun updateQuantity(menuId: String, qty: Int)

    @Query("DELETE FROM cart_table")
    suspend fun clearCart()
}