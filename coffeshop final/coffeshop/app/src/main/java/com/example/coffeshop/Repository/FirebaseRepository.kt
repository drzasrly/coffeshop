package com.example.coffeshop.Repository

import com.example.coffeshop.Domain.MenuModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val menusCollection = db.collection("menus")

    suspend fun getMenuList(): List<MenuModel> {
        return try {
            val querySnapshot = menusCollection.get().await()
            val menuList = querySnapshot.documents.map { document ->
                val menu = document.toObject(MenuModel::class.java)
                menu?.menuId = document.id // Simpan ID dokumen Firebase
                menu ?: MenuModel()
            }.filterNotNull()

            menuList
        } catch (e: Exception) {
            emptyList()
        }
    }
}