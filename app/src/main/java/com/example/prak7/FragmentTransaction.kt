package com.example.prak7

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prak7.databinding.FragmentTransactionBinding

class FragmentTransaction : Fragment(), TransaksiAdapter.OnAdapterListener {

    private lateinit var binding: FragmentTransactionBinding
    private val list = ArrayList<MenuModel>()
    private lateinit var adapter: TransaksiAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransaksiAdapter(list, this)
        binding.recyclerTransaksi.layoutManager = LinearLayoutManager(context)
        binding.recyclerTransaksi.adapter = adapter

        binding.buttonPayNow.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            startActivity(intent)
        }

        updateTotal()
    }

    override fun onDelete(menu: MenuModel) {
        list.remove(menu)
        adapter.notifyDataSetChanged()
        updateTotal()
    }

    override fun onUpdate() {
        updateTotal()
    }

    private fun updateTotal() {
        val totalOrder = TransaksiAdapter.harga
        val tax = totalOrder * 0.1
        val totalPrice = totalOrder + tax

        binding.textTotalOrder.text = "Total Order: Rp $totalOrder"
        binding.textTax.text = "Tax (10%): Rp $tax"
        binding.textTotalPrice.text = "Total Price: Rp $totalPrice"
    }
}