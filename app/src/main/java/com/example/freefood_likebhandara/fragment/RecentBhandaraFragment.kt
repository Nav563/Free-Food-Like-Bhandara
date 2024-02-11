package com.example.freefood_likebhandara.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freefood_likebhandara.activities.BhandaraDetailsActivity
import com.example.freefood_likebhandara.adapter.RecentBhandaraAdapter
import com.example.freefood_likebhandara.databinding.FragmentRecentBhandaraBinding
import com.example.freefood_likebhandara.model.BhandaraModel
import com.example.freefood_likebhandara.viewmodel.RecentBhandaraViewModel
import com.google.gson.Gson

class RecentBhandaraFragment : Fragment() {

    private lateinit var binding: FragmentRecentBhandaraBinding
    private lateinit var adapter: RecentBhandaraAdapter
    private lateinit var listImages: MutableList<String>
    private var listItem = mutableListOf<BhandaraModel>()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var bhandaraViewModel: RecentBhandaraViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecentBhandaraBinding.inflate(layoutInflater)
        listImages = mutableListOf()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        bhandaraViewModel = ViewModelProvider(this)[RecentBhandaraViewModel::class.java]

        // Observe changes in the LiveData
        bhandaraViewModel.recentBhandaraLiveData.observe(viewLifecycleOwner,
            Observer { bhandaraList ->
                // Update the UI with the new list of Bhandara
                adapter.updateItems(bhandaraList)
                progressDialog.dismiss()
            })
        initializeRecyclerView()
        // Fetch data using ViewModel
        bhandaraViewModel.fetchLastThirtyDaysBhandaraData()
        return binding.root
    }

    private fun initializeRecyclerView() {
        adapter = RecentBhandaraAdapter(listItem) { selectedItem ->
            val intent = Intent(requireActivity(), BhandaraDetailsActivity::class.java)
            //intent.putExtra("selectedItemId", selectedItem.bhandaraDate)
            //startActivity(intent)
            val gson = Gson()
            val json = gson.toJson(selectedItem)
            intent.putExtra("selected_item", json)

            startActivity(intent)
        }
        binding.recentBhandaraRecycler.adapter = adapter
        binding.recentBhandaraRecycler.layoutManager = LinearLayoutManager(requireContext())
    }
}