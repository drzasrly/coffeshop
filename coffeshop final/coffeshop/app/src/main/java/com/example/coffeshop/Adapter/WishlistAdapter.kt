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


class WishlistAdapter(
    private val listItemSelected: ArrayList<ItemsModel>,
    private val context: Context,
    // Tambahkan listener yang baru dibuat
    var wishlistChangeListener: WishlistChangeListener? = null
) : RecyclerView.Adapter<WishlistAdapter.Viewholder>() {

    // Menggunakan ManagmentCart untuk logika Add to Cart dan Wishlist
    private val managmentCart = ManagmentCart(context)

    // GANTI nama di bawah ini jika binding Anda bernama ItemWishlistBinding
    inner class Viewholder(val binding: ViewholderWishlistItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        // GANTI nama di bawah ini jika binding Anda bernama ItemWishlistBinding
        val binding = ViewholderWishlistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = listItemSelected[position]

        // 1. Bind Data ke UI Item Wishlist
        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "$${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.picItem)

        // 2. LISTENER: Tombol Delete (Hapus dari Wishlist)
        holder.binding.deleteBtn.setOnClickListener {
            // Asumsi: Anda memiliki fungsi removeFromWishlist yang menerima callback
            managmentCart.removeFromWishlist(item, object : ChangeNumberItemsListener{
                override fun onChanged() {
                    // Perbarui daftar RecyclerView secara lokal
                    listItemSelected.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    notifyItemRangeChanged(holder.adapterPosition, listItemSelected.size)

                    // Beri tahu Activity/Fragment bahwa data telah berubah
                    wishlistChangeListener?.onWishlistChanged()
                    Toast.makeText(context, "Item dihapus dari Wishlist", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 3. LISTENER: Tombol Tambah ke Keranjang
        holder.binding.addToCartBtn.setOnClickListener {
            // Item Wishlist ditambahkan ke Cart dengan jumlah minimal 1
            if (item.numberInCart == 0) item.numberInCart = 1

            managmentCart.insertItems(item) // Tambahkan ke keranjang belanja
            Toast.makeText(holder.itemView.context, "${item.title} ditambahkan ke Keranjang!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}