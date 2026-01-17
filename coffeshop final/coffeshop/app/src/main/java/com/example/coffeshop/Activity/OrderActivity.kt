package com.example.coffeshop.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.OrderAdapter
import com.example.coffeshop.Domain.OrderModel
import com.example.coffeshop.databinding.ActivityOrderBinding // SESUAIKAN DI SINI
import com.google.firebase.database.*

class OrderActivity : AppCompatActivity() {
    // Ubah nama binding agar sesuai dengan nama file XML (activity_order)
    private lateinit var binding: ActivityOrderBinding
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate menggunakan ActivityOrderBinding
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        setupRecyclerView()
        loadOrdersFromFirebase()
    }

    private fun setupRecyclerView() {
        // Sekarang binding.orderRecyclerView akan terbaca karena ID-nya ada di activity_order.xml
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadOrdersFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                if (snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        order?.let { orderList.add(it) }
                    }
                    orderList.reverse()
                    binding.orderRecyclerView.adapter = OrderAdapter(orderList)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}