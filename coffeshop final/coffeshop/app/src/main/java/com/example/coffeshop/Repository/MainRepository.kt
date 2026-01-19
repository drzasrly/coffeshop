package com.example.coffeshop.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coffeshop.Domain.BannerModel
import com.example.coffeshop.Domain.CategoryModel
import com.example.coffeshop.Domain.ItemsModel
import com.google.firebase.database.*

class MainRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<BannerModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(BannerModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value = lists
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value = lists
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return listData
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        // COBA GANTI: Dari "Items" filter popular menjadi langsung ke folder "Popular" 
        // karena biasanya datanya dipisah di folder sendiri di Firebase
        val ref = firebaseDatabase.getReference("Popular")
        
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value = lists
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return listData
    }

    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
        val query = ref.orderByChild("categoryId").equalTo(categoryId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value = lists
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return listData
    }
}
