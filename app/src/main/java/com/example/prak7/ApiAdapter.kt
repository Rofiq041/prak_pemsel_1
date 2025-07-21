package com.example.prak7

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prak7.databinding.CardviewPostBinding

class ApiAdapter(private val list: List<Post>) : RecyclerView.Adapter<ApiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardviewPostBinding.inflate(
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

    inner class ViewHolder(private val binding: CardviewPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post) {
            binding.textTitle.text = item.title
            binding.textBody.text = item.body
        }
    }
}