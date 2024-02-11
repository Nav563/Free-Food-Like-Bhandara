package com.example.freefood_likebhandara.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.freefood_likebhandara.databinding.ActivityChefDetailsBinding
import com.example.freefood_likebhandara.model.BhandaraModel
import com.example.freefood_likebhandara.model.Chef
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class ChefDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChefDetailsBinding
    private lateinit var selectedEvent: String
    private val gson = Gson()
    private lateinit var mobileNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChefDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSpinner()
        val itemJson = intent.getStringExtra("selected_item")
        val listType = object : TypeToken<Chef>() {}.type
        val target2: Chef = gson.fromJson(itemJson, listType)
        displayItemDetails(target2)
        binding.callChef.setOnClickListener {
            callChef()
        }
    }

    private fun callChef() {
        // Check if the mobile number is not null or empty before initiating the call
        if (mobileNumber.isNotBlank()) {
            val dialIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$mobileNumber"))
            // Check if the CALL_PHONE permission is granted before making the call
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(dialIntent)
            } else {
                // Request the CALL_PHONE permission if not granted
                requestPermissions(
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    CALL_PHONE_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun setupSpinner() {
        val data =
            arrayOf(
                "Marriage",
                "Engagement",
                "BirthDay Party",
                "Wedding Anniversary",
                "Bhandara",
                "Other Event"
            )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.eventType.adapter = adapter
        binding.eventType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = data[position]
                selectedEvent = selectedOption
                Toast.makeText(applicationContext, "Selected: $selectedOption", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun displayItemDetails(item: Chef) {
        binding.tvName.text = item.chefName
        binding.tvMobile.text = item.chefMobile
        binding.tvAddress.text = item.chefAddress
        mobileNumber = item.chefMobile.toString()
        Glide.with(this).load(item.chefImage).into(binding.chefImg)
    }

    private fun storeData() {
        val currentdate = Date()
        val a = FirebaseAuth.getInstance().currentUser?.displayName
        val b = FirebaseAuth.getInstance().currentUser?.uid
        val timestamp = Timestamp(currentdate)
        val data = Chef(
            chefName = binding.tvName.text.toString(),
            chefMobile = binding.tvMobile.text.toString(),
            chefAddress = binding.tvAddress.text.toString(),
            //bhandaraDay = selectedEvent,
            timestamp = timestamp
        )
        Firebase.firestore.collection("Chefs").document()
            .set(data)
            .addOnSuccessListener {
                val intent = Intent(this, ChefDetailsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }
    // Handle permission request results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // If CALL_PHONE permission is granted, initiate the call
            binding.callChef.performClick()
        }
    }

    companion object {
        private const val CALL_PHONE_PERMISSION_REQUEST_CODE = 1
    }
}