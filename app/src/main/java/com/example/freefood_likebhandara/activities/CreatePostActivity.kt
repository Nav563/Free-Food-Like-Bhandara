package com.example.freefood_likebhandara.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.freefood_likebhandara.databinding.ActivityCreatePostBinding
import com.example.freefood_likebhandara.model.PostModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var postImg : Uri? = null
    private lateinit var progressDialog: ProgressDialog
    private var postImgUrl: String? = ""
    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            postImg = it.data!!.data
            binding.imgUploadView.setImageURI(postImg)
            binding.imgUploadView.visibility = View.VISIBLE
        }
    }
    private companion object {
        private const val TAG = "BANNER_AD_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.cameraImg.setOnClickListener {
        }
        binding.galleryImg.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }
        binding.sharePost.setOnClickListener {
            validatePostData()
        }
        binding.txtCancel.setOnClickListener {
            onBackPressed()
        }
        binding.contactCheckbox.setOnClickListener{
            contactChecked()
        }
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

    private fun validatePostData() {
        if (postImg != null) {
            uploadImage()
        }else {
            Toast.makeText(this, "Please Select Post Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        progressDialog.show()
        val fileName = UUID.randomUUID().toString()+".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Posts/$fileName")
        refStorage.putFile(postImg!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    postImgUrl = image.toString()
                    storeData()
                }
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this, "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun contactChecked(){
    }

    private fun storeData() {
        progressDialog.show()
        val postCurrentDate = Date().toFormattedString()
        val currentDate = Date()
        val timeStamp = Timestamp(currentDate)
        val a = FirebaseAuth.getInstance().currentUser?.displayName
        val b = FirebaseAuth.getInstance().currentUser?.uid
        val c = FirebaseAuth.getInstance().currentUser?.phoneNumber
        val data = PostModel(
            postImage = postImgUrl.toString(),
            postDescription = binding.edtPostDescription.text.toString(),
            timestamp = timeStamp,
            postDate = postCurrentDate,
            postedByID = b.toString(),
            postedByName = a.toString(),
            phone = c.toString(),
            canPublicContact = binding.contactCheckbox.isChecked)
        Firebase.firestore.collection("Posts").document()
            .set(data)
            .addOnSuccessListener {
                val intent = Intent(this, CreatePostActivity::class.java)
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
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
fun Date.toFormattedString(): String {
    // Format the date as "dd-MM-yyyy HH:mm"
    val sdf = SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault())
    return sdf.format(this)
}