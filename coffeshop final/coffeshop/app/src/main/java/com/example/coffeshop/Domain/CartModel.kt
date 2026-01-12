package com.example.coffeshop.Domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cart_table")
data class CartModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,              // ID LOKAL (Room)
    val menu_id: String,          // ID SERVER
    val name: String,
    val price: String,
    val quantity: Int,
    val size: String,
    val imageUrl: String?
) : Serializable
