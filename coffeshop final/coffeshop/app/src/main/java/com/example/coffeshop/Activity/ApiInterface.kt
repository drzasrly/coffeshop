// Activity/ApiInterface.kt
package com.example.coffeshop.Activity

import com.example.coffeshop.Domain.WishlistRequest
import com.example.coffeshop.Domain.WishlistResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @POST("wishlist")
    fun addWishlist(
        @Header("Authorization") token: String,
        @Body request: WishlistRequest
    ): Call<WishlistResponse>
}