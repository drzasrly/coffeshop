package com.example.coffeshop.Domain

data class OrderItemRequest(
    val menu_id: String,
    val quantity: Int,
    val price: Double,
    val size: String
)
