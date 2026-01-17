package com.example.coffeshop.Domain

data class OrderResponse(
    val success: Boolean,
    val message: String,
    val data: OrderData
)

data class OrderData(
    val id: Int,
    val total_price: Double,
    val status: String,
    val items: List<OrderItemRequest>
)
