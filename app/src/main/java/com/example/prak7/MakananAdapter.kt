package com.example.prak7

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prak7.databinding.CardviewMakananBinding

class MakananAdapter(private val list: ArrayList<MenuModel>) :
    RecyclerView.Adapter<MakananAdapter.MakananViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val binding = CardviewMakananBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MakananViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class MakananViewHolder(private val binding: CardviewMakananBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MenuModel) {
            val id: Int = data.id
            val nama: String = data.name
            val harga: Int = data.price
            val gambar: Bitmap = data.image

            binding.textIdMakanan.text = id.toString()
            binding.textNamaMakanan.text = nama
            binding.textHargaMakanan.text = harga.toString()
            binding.imageMakanan.setImageBitmap(gambar)

            binding.buttonTambah.setOnClickListener {
                val cek = TransaksiAdapter.listId.find { it == id }
                if (cek == null) {
                    TransaksiAdapter.listId.add(id)
                    TransaksiAdapter.listNama.add(nama)
                    TransaksiAdapter.listHarga.add(harga)
                    TransaksiAdapter.listFoto.add(gambar)
                    TransaksiAdapter.listJumlah.add(1)
                    TransaksiAdapter.harga += harga
                    TransaksiAdapter.jumlah += 1
                } else {
                    val index = TransaksiAdapter.listId.indexOf(id)
                    TransaksiAdapter.listJumlah[index] += 1
                    TransaksiAdapter.harga += harga
                    TransaksiAdapter.jumlah += 1
                }
            }
        }
    }
}
