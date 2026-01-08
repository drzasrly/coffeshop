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
    private var listItemSelected: ArrayList<CartModel>,
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

    fun updateData(newList: List<CartModel>) {
        listItemSelected.clear()
        listItemSelected.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = listItemSelected[position]

        holder.binding.apply {
            titleTxt.text = "${item.name} (${item.size})"
            feeEachItem.text = "$${item.price}"

            val priceValue = item.price.toDoubleOrNull() ?: 0.0
            val totalItemPrice = Math.round((item.quantity * priceValue) * 100.0) / 100.0
            totalEachItem.text = "$$totalItemPrice"
            numberInCartTxt.text = item.quantity.toString()

            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .apply(RequestOptions().transform(CenterCrop()))
                .into(picCart)

            // ... di dalam onBindViewHolder ...
            plusBtn.setOnClickListener {
                val newQty = item.quantity + 1
                // Kirim ID (Primary Key) dan newQty
                cartViewModel.updateQuantity(item.id, newQty)
                changeNumberItemsListener?.onChanged()
            }

            minusBtn.setOnClickListener {
                if (item.quantity > 1) {
                    val newQty = item.quantity - 1
                    cartViewModel.updateQuantity(item.id, newQty)
                } else {
                    cartViewModel.removeFromCart(item.menu_id)
                }
                changeNumberItemsListener?.onChanged()
            }
// HAPUS SEMUA KODE DI BAWAH KURUNG KURAWAL TERAKHIR KELAS CartAdapter

            removeItemBtn.setOnClickListener {
                cartViewModel.removeFromCart(item.menu_id)
                changeNumberItemsListener?.onChanged()
            }
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}
