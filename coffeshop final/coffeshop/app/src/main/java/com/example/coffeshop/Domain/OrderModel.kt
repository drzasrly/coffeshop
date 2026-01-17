package com.example.coffeshop.Domain

import java.io.Serializable

data class OrderModel(
    var orderId: String = "",
    var date: String = "",
    var status: String = "",
    var totalPrice: Double = 0.0,
    var address: String = "",
    var timestamp: Long = 0L, // Untuk melacak waktu pesanan
    var items: ArrayList<OrderItemModel> = ArrayList()
) : Serializable

data class OrderItemModel(
    var title: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var picUrl: String? = "", // Sesuai dengan key di Firebase
    var size: String = ""
) : Serializable