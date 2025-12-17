package com.example.coffeshop.Repository

import LoginResponse
import com.example.coffeshop.Domain.*
import retrofit2.http.*
import retrofit2.Call

interface ApiService {

    // --- AUTHENTICATION ---
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    // --- WISHLIST CRUD (PROTECTED BY JWT) ---

    // Mengambil semua item wishlist milik user

    @POST("wishlist")
    fun addWishlist(
        @Header("Authorization") token: String,
        @Body request: WishlistRequest
    ): Call<WishlistResponse>
    @GET("wishlist")
    suspend fun getWishlistItems(): WishlistResponse

    // Menambah item ke wishlist (menu_id adalah String dari Firebase)
    @POST("wishlist")
    suspend fun addToWishlist(@Body request: WishlistRequest): WishlistResponse

    // Menghapus item dari wishlist menggunakan ID baris SQL
    @DELETE("wishlist/{id}")
    suspend fun deleteWishlistItem(@Path("id") wishlistId: Int): WishlistResponse
}