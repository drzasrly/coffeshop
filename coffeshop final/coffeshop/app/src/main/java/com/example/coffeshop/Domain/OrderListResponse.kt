// Domain/OrderListResponse.kt
data class OrderListResponse(
    val success: Boolean,
    val data: List<OrderResponseData>
)

data class OrderResponseData(
    val id: Int,
    val total_price: Double,
    val status: String, // Ini yang menyimpan status "proses" atau "selesai"
    val address: String,
    val created_at: String
)