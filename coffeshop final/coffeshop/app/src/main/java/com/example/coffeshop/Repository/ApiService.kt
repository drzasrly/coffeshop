package com.example.coffeshop.Repository

import LoginResponse
import com.example.coffeshop.Domain.*
import okhttp3.ResponseBody
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
    ): Call<ResponseBody> // Gunakan ResponseBody agar tidak dipaksa parsing otomatis yang bikin error
    @GET("wishlist")
    suspend fun getWishlistItems(): WishlistResponse

    // Menambah item ke wishlist (menu_id adalah String dari Firebase)
    @POST("wishlist")
    suspend fun addToWishlist(@Body request: WishlistRequest): WishlistResponse

    // Menghapus item dari wishlist menggunakan ID baris SQL
    @DELETE("wishlist/{id}")
    suspend fun deleteWishlistItem(@Path("id") wishlistId: Int): WishlistResponse
    @DELETE("wishlist/{menu_id}")
    fun removeFromWishlist(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String
    ): Call<ResponseBody>

    // Get All Cart
    @GET("cart")
    fun getCart(@Header("Authorization") token: String): Call<ResponseBody>

    // Add/Update Cart
    @POST("cart")
    fun addCart(
        @Header("Authorization") token: String,
        @Body request: CartRequest
    ): Call<ResponseBody>

    // Delete Cart
    @DELETE("cart/{menu_id}")
    fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String
    ): Call<ResponseBody>
}