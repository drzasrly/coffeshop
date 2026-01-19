package com.example.coffeshop.Domain // Pastikan package ini sesuai folder kamu

import com.google.gson.annotations.SerializedName

data class OrderListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<OrderResponseData>
)

data class OrderResponseData(
    @SerializedName("id") val id: Int,
    @SerializedName("total_price") val total_price: Double,
    @SerializedName("status") val status: String,
    @SerializedName("address") val address: String,
    @SerializedName("created_at") val created_at: String
)