package com.example.coffeshop.Domain

data class OrderRequest(
    val total_price: Double,
    val address: String?,
    val items: List<OrderItemRequest>
)
