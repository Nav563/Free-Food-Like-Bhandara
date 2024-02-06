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
import com.example.freefood_likebhandara.model.Chef
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        if (!mobileNumber.isNullOrBlank()) {
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