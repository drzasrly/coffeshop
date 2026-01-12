package com.example.coffeshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.coffeshop.Domain.CartModel
import com.example.coffeshop.databinding.ViewholderCartBinding
import com.example.coffeshop.viewModel.CartViewModel

class CartAdapter(
    private var listItemSelected: ArrayList<CartModel>,
    context: Context,
    private val cartViewModel: CartViewModel
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
            totalEachItem.text = "$${priceValue * item.quantity}"
            numberInCartTxt.text = item.quantity.toString()

            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .apply(RequestOptions().transform(CenterCrop()))
                .into(picCart)

            // ✅ FIX UTAMA (TIDAK UBAH UI MANUAL)
            plusBtn.setOnClickListener {
                cartViewModel.updateQuantity(
                    menuId = item.menu_id,
                    localId = item.id,
                    newQty = item.quantity + 1
                )
            }

            minusBtn.setOnClickListener {
                if (item.quantity > 1) {
                    cartViewModel.updateQuantity(
                        menuId = item.menu_id,
                        localId = item.id,
                        newQty = item.quantity - 1
                    )
                } else {
                    cartViewModel.removeFromCart(item.menu_id)
                }
            }

            removeItemBtn.setOnClickListener {
                cartViewModel.removeFromCart(item.menu_id)
            }
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}
