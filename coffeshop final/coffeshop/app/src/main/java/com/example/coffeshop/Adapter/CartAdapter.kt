package com.example.coffeshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.coffeshop.Domain.CartModel
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.databinding.ViewholderCartBinding
import com.example.coffeshop.viewModel.CartViewModel

class CartAdapter(
    private val listItemSelected: ArrayList<CartModel>,
    context: Context,
    private val cartViewModel: CartViewModel,
    var changeNumberItemsListener: ChangeNumberItemsListener? = null
) : RecyclerView.Adapter<CartAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = listItemSelected[position]

        holder.binding.apply {
            // Sinkronisasi Data ke View
            titleTxt.text = item.name
            feeEachItem.text = "$${item.price}"

            // Perhitungan total harga per baris
            val priceValue = item.price.toDoubleOrNull() ?: 0.0
            val totalItemPrice = Math.round((item.quantity * priceValue) * 100.0) / 100.0
            totalEachItem.text = "$$totalItemPrice"

            // ID di XML kamu adalah numberInCartTxt
            numberInCartTxt.text = item.quantity.toString()

            // Load Gambar ke picCart
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .apply(RequestOptions().transform(CenterCrop()))
                .into(picCart)

            // Logika Tombol Plus (ID: plusBtn)
            plusBtn.setOnClickListener {
                val newQty = item.quantity + 1
                cartViewModel.updateQuantity(item.menu_id, newQty)
                changeNumberItemsListener?.onChanged()
            }

            // Logika Tombol Minus (ID: minusBtn)
            minusBtn.setOnClickListener {
                if (item.quantity > 1) {
                    val newQty = item.quantity - 1
                    cartViewModel.updateQuantity(item.menu_id, newQty)
                } else {
                    // Jika tinggal 1, hapus dari database
                    cartViewModel.removeFromCart(item.menu_id)
                }
                changeNumberItemsListener?.onChanged()
            }

            // Logika Tombol Remove (ID: removeItemBtn)
            removeItemBtn.setOnClickListener {
                cartViewModel.removeFromCart(item.menu_id)
                changeNumberItemsListener?.onChanged()
            }
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}

private fun CartViewModel.updateQuantity(
    menuId: String,
    newQty: Int
) {
}
