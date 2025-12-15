package com.example.coffeshop.Helper

import android.content.Context
import android.widget.Toast
import com.example.coffeshop.Domain.ItemsModel

class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    // --- Kunci Khusus ---
    private val CART_KEY = "CartList"
    private val WISHLIST_KEY = "WishlistList"

    // =====================================================
    // LOGIKA CART (KERANJANG) - TETAP SAMA
    // =====================================================

    fun insertItems(item: ItemsModel) {
        var listItem = getListCart()
        val existAlready = listItem.any { it.title == item.title }
        val index = listItem.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listItem[index].numberInCart = item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject(CART_KEY, listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(CART_KEY) ?: arrayListOf()
    }

    fun minusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listItems[position].numberInCart == 1) {
            listItems.removeAt(position)
        } else {
            listItems[position].numberInCart--
        }
        tinyDB.putListObject(CART_KEY, listItems)
        listener.onChanged()
    }

    fun removeItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItems.removeAt(position)
        tinyDB.putListObject(CART_KEY, listItems)
        listener.onChanged()
    }

    fun plusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItems[position].numberInCart++
        tinyDB.putListObject(CART_KEY, listItems)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (item in listItem) {
            fee += item.price * item.numberInCart
        }
        return fee
    }

    // =====================================================
    // LOGIKA WISHLIST / FAVORIT (DIPERBAIKI)
    // =====================================================

    // Menggunakan nama getWishlist() agar sinkron dengan Activity
    fun getWishlist(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(WISHLIST_KEY) ?: arrayListOf()
    }

    fun addToWishlist(item: ItemsModel) {
        var listWishlist = getWishlist()
        val existAlready = listWishlist.any { it.title == item.title }

        if (!existAlready) {
            item.numberInCart = 1
            listWishlist.add(item)
            tinyDB.putListObject(WISHLIST_KEY, listWishlist)
            Toast.makeText(context, "Ditambahkan ke Wishlist Anda", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Sudah ada di Wishlist!", Toast.LENGTH_SHORT).show()
        }
    }

    // Menerima ChangeNumberItemsListener agar sinkron dengan WishlistAdapter
    fun removeFromWishlist(item: ItemsModel, listener: ChangeNumberItemsListener) {
        val listWishlist = getWishlist()
        val indexToRemove = listWishlist.indexOfFirst { it.title == item.title }

        if (indexToRemove != -1) {
            listWishlist.removeAt(indexToRemove)
            tinyDB.putListObject(WISHLIST_KEY, listWishlist)
            Toast.makeText(context, "Dihapus dari Wishlist Anda", Toast.LENGTH_SHORT).show()
            listener.onChanged() // Panggil listener setelah berhasil dihapus
        }
    }

    fun isItemInWishlist(item: ItemsModel): Boolean {
        return getWishlist().any { it.title == item.title }
    }
}