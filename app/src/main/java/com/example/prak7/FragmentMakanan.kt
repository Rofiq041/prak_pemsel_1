package com.example.prak7

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.prak7.DatabaseHelper

class FragmentMakanan : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_makanan, container, false)
        setupRecyclerView(view)

        val buttonAdd: FloatingActionButton = view.findViewById(R.id.buttonAddMenu)
        buttonAdd.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, AddMenuActivity::class.java))
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        view?.let { setupRecyclerView(it) }
    }

    private fun setupRecyclerView(view: View) {
        val rvMakanan: RecyclerView = view.findViewById(R.id.recyclerMakanan)
        val databaseHelper = DatabaseHelper(requireContext())
        val listData = databaseHelper.showMenu()
        rvMakanan.layoutManager = LinearLayoutManager(activity)
        rvMakanan.adapter = MakananAdapter(listData)
    }
}