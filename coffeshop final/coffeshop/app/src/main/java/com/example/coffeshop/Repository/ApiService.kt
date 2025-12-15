package com.example.coffeshop.Repository

import com.example.coffeshop.Domain.*
import retrofit2.http.*

interface ApiService {

    // AUTHENTICATION
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    // WISHLIST CRUD (PROTECTED)
    @POST("wishlist")
    suspend fun addToWishlist(@Body request: WishlistRequest): GenericResponse

    @GET("wishlist")
    suspend fun getWishlistItems(): List<WishlistModel>

    @DELETE("wishlist/{id}")
    suspend fun deleteWishlistItem(@Path("id") wishlistId: Int): GenericResponse
}