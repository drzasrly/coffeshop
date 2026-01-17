package com.example.coffeshop.Domain

import java.io.Serializable

data class OrderModel(
    var orderId: String = "",
    var date: String = "",
    var status: String = "Pending",
    var totalPrice: Double = 0.0,
    // Gunakan ArrayList agar bisa membaca format [ ... ] dari JSON
    var items: ArrayList<ItemsModel> = ArrayList()
) : Serializable