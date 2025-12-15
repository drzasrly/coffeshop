package com.example.coffeshop.Domain

data class MenuModel(
    var menuId: String? = null,
    var nama_menu: String? = null,
    var deskripsi: String? = null,
    var harga: Int? = 0,
    var kategori: String? = null
) {
    constructor() : this(null, null, null, 0, null)
}