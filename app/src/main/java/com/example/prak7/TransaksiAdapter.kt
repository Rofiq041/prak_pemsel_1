package com.example.prak7

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prak7.databinding.CardviewTransactionBinding

class TransaksiAdapter(
    private val list: ArrayList<MenuModel>,
    private val listener: OnAdapterListener
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    companion object {
        var listId = mutableListOf<Int>()
        var listNama = mutableListOf<String>()
        var listHarga = mutableListOf<Int>()
        var listJumlah = mutableListOf<Int>()
        var listFoto = mutableListOf<android.graphics.Bitmap>()
        var harga = 0
        var jumlah = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardviewTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: CardviewTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuModel) {
            binding.textNamaTransaksi.text = item.name
            binding.textHargaTransaksi.text = item.price.toString()
            binding.imageTransaksi.setImageBitmap(item.image)

            binding.buttonHapusTransaksi.setOnClickListener {
                listener.onDelete(item)
            }

            binding.imageButtonPlus.setOnClickListener {
                val qty = binding.textQtyTransaksi.text.toString().toInt()
                binding.textQtyTransaksi.text = (qty + 1).toString()
                harga += item.price
                jumlah++
                listener.onUpdate()
            }

            binding.imageButtonMinus.setOnClickListener {
                val qty = binding.textQtyTransaksi.text.toString().toInt()
                if (qty > 1) {
                    binding.textQtyTransaksi.text = (qty - 1).toString()
                    harga -= item.price
                    jumlah--
                    listener.onUpdate()
                }
            }
        }
    }

    interface OnAdapterListener {
        fun onDelete(menu: MenuModel)
        fun onUpdate()
    }
}