package com.example.coffeshop.Repository

import OrderListResponse
import retrofit2.Response
import com.example.coffeshop.Domain.*
import okhttp3.ResponseBody
import retrofit2.http.*
import retrofit2.Call

interface ApiService {

    // --- AUTHENTICATION ---
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    // --- CHANGE PASSWORD ---
    @FormUrlEncoded
    @POST("change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("current_password") oldPass: String,
        @Field("new_password") newPass: String,
        @Field("new_password_confirmation") confirmPass: String
    ): Response<ResponseBody>

    // --- PROFILE ---
    // PASTIKAN HANYA ADA SATU FUNGSI updateProfile DI SINI
    @FormUrlEncoded
    @POST("update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Field("name") name: String
    ): Response<UserResponse>

    // --- WISHLIST ---
    @POST("wishlist")
    fun addWishlist(
        @Header("Authorization") token: String,
        @Body request: WishlistRequest
    ): Call<ResponseBody>

    @GET("wishlist")
    suspend fun getWishlistItems(): WishlistResponse

    @DELETE("wishlist/{menu_id}")
    fun removeFromWishlist(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String
    ): Call<ResponseBody>

    // --- CART ---
    @GET("cart")
    fun getCart(@Header("Authorization") token: String): Call<ResponseBody>

    @POST("cart")
    fun addCart(
        @Header("Authorization") token: String,
        @Body request: CartRequest
    ): Call<ResponseBody>

    @PATCH("cart/{menu_id}")
    fun updateCartQuantity(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String,
        @Body request: CartRequest
    ): Call<ResponseBody>

    @DELETE("cart/{menu_id}")
    fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("menu_id") menuId: String
    ): Call<ResponseBody>

    // --- ORDERS ---
    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Response<OrderResponse>

    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") token: String
    ): Response<OrderListResponse>

}

