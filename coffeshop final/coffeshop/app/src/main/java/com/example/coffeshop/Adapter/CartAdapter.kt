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
    private val cartViewModel: CartViewModel,
    private val onItemSelected: () -> Unit // Callback untuk update total di Activity
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
            // 1. Set Data Dasar
            titleTxt.text = "${item.name} (${item.size})"
            val cleanPrice = item.price.replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0.0
            feeEachItem.text = "Rp ${cleanPrice.toInt()}"
            totalEachItem.text = "Rp ${(cleanPrice * item.quantity).toInt()}"
            numberInCartTxt.text = item.quantity.toString()

            // 2. Load Gambar
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .apply(RequestOptions().transform(CenterCrop()))
                .into(picCart)

            // 3. Handle CheckBox (Sangat Penting!)
            // Reset listener dulu ke null agar tidak terpanggil saat recycling view
            checkItem.setOnCheckedChangeListener(null)
            checkItem.isChecked = item.isSelected

            checkItem.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked
                onItemSelected() // Beritahu Activity untuk hitung ulang total
            }

            // 4. Tombol Tambah
            plusBtn.setOnClickListener {
                cartViewModel.updateQuantity(
                    menuId = item.menu_id,
                    localId = item.id,
                    newQty = item.quantity + 1
                )
            }

            // 5. Tombol Kurang
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

            // 6. Tombol Hapus Item
            removeItemBtn.setOnClickListener {
                cartViewModel.removeFromCart(item.menu_id)
            }
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}