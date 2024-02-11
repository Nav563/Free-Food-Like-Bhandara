package com.example.freefood_likebhandara.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.freefood_likebhandara.adapter.ViewPagerAdapter
import com.example.freefood_likebhandara.databinding.FragmentFoodBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class FoodFragment : Fragment() {

    private lateinit var binding: FragmentFoodBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private lateinit var toolbar: Toolbar

    private companion object {
        private const val TAG = "BANNER_AD_TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFoodBinding.inflate(layoutInflater)
        tabLayout = binding.tabs
        viewPager = binding.viewPagerV2
        toolbar = binding.topAppBar1
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            title = "Bhandara Location"
            setDisplayHomeAsUpEnabled(true)
        }
        viewPagerAdapter = ViewPagerAdapter(requireActivity())
        binding.viewPagerV2.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "NEAR YOU"
                1 -> tab.text = "DAILY BHANDARA"
                // Add more tabs if needed
            }
        }.attach()

        // Initialize Mobile Ads SDK
        MobileAds.initialize(requireContext()) {
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

        return binding.root
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