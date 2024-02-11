package com.example.freefood_likebhandara.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.freefood_likebhandara.databinding.ActivityBhandaraDetailsBinding
import com.example.freefood_likebhandara.fragment.FoodFragment
import com.example.freefood_likebhandara.model.BhandaraModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.ocpsoft.prettytime.PrettyTime


class BhandaraDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBhandaraDetailsBinding
    private val gson = Gson()
    private companion object {
        private const val TAG = "BANNER_AD_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBhandaraDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        val itemJson = intent.getStringExtra("selected_item")
        val listType = object : TypeToken<BhandaraModel>() {}.type
        val target2: BhandaraModel = gson.fromJson(itemJson, listType)
        displayItemDetails(target2)
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {
            Log.d(TAG, "onInitializationCompleted")
        }
        // Set Your test device. Check your logcat output for the hashed device Id to get test ads on physical device
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(
                    listOf(
                        "PLACE_TEST_DEVICE_ID_1_HERE",
                        "PLACE_TEST_DEVICE_ID_2_HERE"
                    )
                )
                .build()
        )
        showBannerAd()

    }

    private fun displayItemDetails(item: BhandaraModel) {
        // Display details in UI
        binding.sharedBy.text = item.postedByName
        binding.bhandaraAddress.text = item.bhandaraAddress
        binding.bhandaraDay.text = item.bhandaraDay
        binding.bhandaraDate.text = item.bhandaraDate
        binding.startTime.text = item.startTime
        bind(item)
        binding.endTime.text = item.endTime
        Glide.with(this).load(item.bhandaraImage).into(binding.bhandaraImage)

        binding.mapLocation.setOnClickListener {
            navigateToLocation(item.latitude, item.longitude)
        }
    }

    private fun navigateToLocation(lat: Double, long: Double) {
        val geoUri = Uri.parse("geo:$lat,$long")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Handle case where Google Maps is not installed
            Toast.makeText(this, "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun bind(item: BhandaraModel) {
        val date = item.timestamp.toDate()
        val prettyTime = PrettyTime()
        val formattedDate = prettyTime.format(date)
        // Display the formatted date
        binding.tvBhandaraDate.text = "  $formattedDate"
    }
    private fun showBannerAd() {
        // Ad Request
        val adRequest = AdRequest.Builder().build()
        // Load Ad
        binding.bannerAd?.loadAd(adRequest)
        binding.bannerAd?.adListener = object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d(TAG, "onAdClosed : ")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.d(TAG, "onAdFailedToLoad : ${adError.message}")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(TAG, "onAdImpression")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "onAdLoaded")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(TAG, "onAdOpened")
            }
        }
    }

    override fun onPause() {
        binding.bannerAd?.pause()
        super.onPause()
        Log.d(TAG, "onAdPaused :")
    }

    override fun onResume() {
        binding.bannerAd?.resume()
        super.onResume()
        Log.d(TAG, "onAdResume :")
    }

    override fun onDestroy() {
        binding.bannerAd?.destroy()
        super.onDestroy()
        Log.d(TAG, "onAdDestroy :")
    }
}