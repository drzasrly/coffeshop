package com.example.coffeshop.Domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "wishlist_table")
data class WishlistModel(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0, // Gunakan autoGenerate agar SQL membuatkan ID 1, 2, 3 otomatis
    val id: Int? = null,        // ID dari Laravel (opsional)
    val user_id: Int = 0,
    val menu_id: String = "",   // ID Menu dari Firebase ("0", "1", "2")
    val name: String = "",
    val price: String = "",
    val imageUrl: String? = null
) : Serializable