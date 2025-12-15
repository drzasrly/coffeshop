package com.example.coffeshop.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coffeshop.Adapter.ItemListCategoryAdapter
import com.example.coffeshop.databinding.ActivityItemsListBinding
import com.example.coffeshop.viewModel.MainViewModel

class ItemsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemsListBinding

    // Inisialisasi ViewModel dengan cara yang benar
    private val viewModel: MainViewModel by viewModels()

    private var id: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundles()
        initList()
    }

    private fun initList() {
        binding.apply {
            progressBar.visibility = View.VISIBLE

            backBtn.setOnClickListener {
                finish()
            }

            viewModel.loadItems(id).observe(this@ItemsListActivity) { itemList ->
                // PENTING:
                // 'listView' di bawah ini merujuk pada android:id="@+id/listView" di XML Anda.
                // Jika di XML ID-nya 'ListView' (huruf L besar), ubah kata 'listView' di bawah menjadi 'ListView'.
                // Pastikan view di XML adalah <androidx.recyclerview.widget.RecyclerView>

                listView.layoutManager = GridLayoutManager(this@ItemsListActivity, 2)
                listView.adapter = ItemListCategoryAdapter(itemList)

                progressBar.visibility = View.GONE
            }
        }
    }

    private fun getBundles() {
        // Menggunakan Elvis operator (?:) untuk mencegah crash jika data null
        id = intent.getStringExtra("id") ?: ""
        title = intent.getStringExtra("title") ?: ""

        binding.categoryTxt.text = title
    }
}