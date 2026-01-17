package com.example.coffeshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.coffeshop.Domain.ItemsModel
import com.example.coffeshop.Helper.ChangeNumberItemsListener
import com.example.coffeshop.Helper.ManagmentCart
// GANTI nama di bawah ini jika binding Anda bernama ItemWishlistBinding
import com.example.coffeshop.databinding.ViewholderWishlistItemBinding
import com.example.coffeshop.viewModel.WishlistViewModel

class WishlistAdapter(
    private val listItemSelected: ArrayList<ItemsModel>,
    private val context: Context,
    // Tambahkan ViewModel di sini
    private val viewModel: WishlistViewModel,
    var wishlistChangeListener: WishlistChangeListener? = null
) : RecyclerView.Adapter<WishlistAdapter.Viewholder>() {

    private val managmentCart = ManagmentCart(context)

    inner class Viewholder(val binding: ViewholderWishlistItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderWishlistItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = listItemSelected[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "$${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.picItem)

        // Tombol Delete
        holder.binding.deleteBtn.setOnClickListener {
            val currentMenuId = item.id.toString()

            // 1. Hapus Permanen di Laravel & Room via ViewModel
            viewModel.removeFromWishlist(currentMenuId)

            // 2. Hapus dari UI Helper (ManagementCart SharedPreferences)
            managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    // 3. Update tampilan RecyclerView
                    val currentPos = holder.adapterPosition
                    if (currentPos != RecyclerView.NO_POSITION) {
                        listItemSelected.removeAt(currentPos)
                        notifyItemRemoved(currentPos)
                        notifyItemRangeChanged(currentPos, listItemSelected.size)

                        wishlistChangeListener?.onWishlistChanged()
                        Toast.makeText(context, "Item dihapus dari favorit", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        holder.binding.addToCartBtn.setOnClickListener {
            if (item.numberInCart == 0) item.numberInCart = 1
            managmentCart.insertItems(item)
            Toast.makeText(holder.itemView.context, "${item.title} masuk keranjang!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}