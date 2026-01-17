package com.example.coffeshop.Domain

// File ini murni untuk menangkap data dari API Laravel
data class CartResponse(
    val success: Boolean,
    val data: List<CartServerModel>
)

data class CartServerModel(
    val id: Int,
    val menu_id: Int,
    val quantity: Int,
    val menu: ItemsModel? // Ini sekarang tidak akan error karena tidak ada @Entity
)