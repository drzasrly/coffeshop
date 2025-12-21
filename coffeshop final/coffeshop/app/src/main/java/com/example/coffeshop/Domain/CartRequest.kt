package com.example.coffeshop.Domain

data class CartRequest(
    val menu_id: String,
    val quantity: Int
)