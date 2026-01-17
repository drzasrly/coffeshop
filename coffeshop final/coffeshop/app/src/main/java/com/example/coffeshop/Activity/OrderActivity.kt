package com.example.coffeshop.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeshop.Adapter.OrderAdapter
import com.example.coffeshop.Domain.OrderModel
import com.example.coffeshop.databinding.ActivityOrderBinding
import com.google.firebase.database.*

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        setupRecyclerView()
        loadOrdersFromFirebase()
    }

    private fun setupRecyclerView() {
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadOrdersFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                if (snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        try {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let {
                                it.orderId = orderSnapshot.key ?: ""
                                orderList.add(it)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("OrderActivity", "Error parsing: ${e.message}")
                        }
                    }

                    // MEMBALIK LIST: Pesanan terbaru muncul paling atas
                    orderList.reverse()

                    binding.orderRecyclerView.adapter = OrderAdapter(orderList)
                } else {
                    Toast.makeText(this@OrderActivity, "Belum ada pesanan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrderActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}