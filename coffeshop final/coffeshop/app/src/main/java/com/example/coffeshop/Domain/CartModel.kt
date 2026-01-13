package com.example.coffeshop.Domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cart_table")
data class CartModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val menu_id: String,
    val name: String,
    val price: String,
    val quantity: Int,
    val size: String,
    val imageUrl: String?,
    var isSelected: Boolean = false
) : Parcelable // Ubah dari Serializable ke Parcelable