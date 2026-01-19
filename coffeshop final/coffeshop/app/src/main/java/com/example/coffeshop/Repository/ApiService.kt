package com.example.coffeshop.Repository

import com.example.coffeshop.Domain.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.Call

interface ApiService {

    // --- AUTHENTICATION ---
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    // --- CHANGE PASSWORD ---
    // Interceptor akan otomatis menambahkan Header Authorization "Bearer <token>"
    @FormUrlEncoded
    @POST("change-password")
    suspend fun changePassword(
        @Field("current_password") oldPass: String,
        @Field("new_password") newPass: String,
        @Field("new_password_confirmation") confirmPass: String
    ): Response<ResponseBody>

    // --- PROFILE ---
    @FormUrlEncoded
    @POST("update-profile")
    suspend fun updateProfile(
        @Field("name") name: String
    ): Response<UserResponse>

    // --- WISHLIST ---
    @POST("wishlist")
    fun addWishlist(
        @Body request: WishlistRequest
    ): Call<ResponseBody>

    @GET("wishlist")
    suspend fun getWishlistItems(): WishlistResponse

    @DELETE("wishlist/{menu_id}")
    fun removeFromWishlist(
        @Path("menu_id") menuId: String
    ): Call<ResponseBody>

    // --- CART ---
    @GET("cart")
    fun getCart(): Call<ResponseBody>

    @POST("cart")
    fun addCart(
        @Body request: CartRequest
    ): Call<ResponseBody>

    @PATCH("cart/{menu_id}")
    fun updateCartQuantity(
        @Path("menu_id") menuId: String,
        @Body request: CartRequest
    ): Call<ResponseBody>

    @DELETE("cart/{menu_id}")
    fun removeFromCart(
        @Path("menu_id") menuId: String
    ): Call<ResponseBody>

    // --- ORDERS ---
    @POST("orders")
    suspend fun createOrder(
        @Body request: OrderRequest
    ): Response<OrderResponse>

    @GET("orders")
    suspend fun getOrders(): Response<OrderListResponse>
}