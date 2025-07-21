package com.example.prak7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.prak7.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalOrder = TransaksiAdapter.harga
        val tax = totalOrder * 0.1
        val totalPrice = totalOrder + tax

        binding.textViewTotalPurchase.text = "Total Purchase: Rp $totalPrice"

        binding.buttonFinish.setOnClickListener {
            val cash = binding.editTextCash.text.toString().toInt()
            if (cash >= totalPrice) {
                val change = cash - totalPrice
                binding.textViewChange.text = "Change: Rp $change"

                val dbHelper = DatabaseHelper(this)
                dbHelper.addTransaction()

                TransaksiAdapter.listId.clear()
                TransaksiAdapter.listNama.clear()
                TransaksiAdapter.listHarga.clear()
                TransaksiAdapter.listJumlah.clear()
                TransaksiAdapter.listFoto.clear()
                TransaksiAdapter.harga = 0
                TransaksiAdapter.jumlah = 0

                Toast.makeText(this, "Transaction Success", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cash is not enough", Toast.LENGTH_SHORT).show()
            }
        }
    }
}