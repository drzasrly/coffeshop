package com.example.coffeshop.Domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cart_table")
data class CartModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val menu_id: String,
    val name: String,
    val price: String, // Kita gunakan String agar konsisten dengan Wishlist
    val imageUrl: String?,
    var quantity: Int = 1
) : Serializable