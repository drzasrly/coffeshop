package com.example.coffeshop.Domain

import java.io.Serializable

data class ItemsModel(
    var id: Int = 0, // Ubah Any menjadi Int agar cocok dengan Database SQL
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    var extra: String = "",
    var quantity: Int = 0
) : Serializable