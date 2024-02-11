package com.example.freefood_likebhandara.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.freefood_likebhandara.MainActivity
import com.example.freefood_likebhandara.databinding.ActivityEditProfileBinding
import com.example.freefood_likebhandara.model.UserModel
import com.example.freefood_likebhandara.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        // Observe changes in the user list and update the UI
        userViewModel.currentUserLiveData.observe(this, Observer { users ->
            // Update UI with the list of users
            updateUI(users)
        })
        // Trigger the ViewModel to fetch user data
        userViewModel.fetchCurrentUsers()

        binding.homeLayout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.profileLayout.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.dailyBhandaraPostLayout.setOnClickListener {
            startActivity(Intent(this, ShareBhandaraLocationActivity::class.java))
        }
        binding.myUploadedListLayout.setOnClickListener {

        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
        val appVersion = getAppVersion()
        binding.appVersion.text = "App Version : $appVersion"
    }

    private fun updateUI(currentUser: UserModel?) {
        // Update UI elements with the list of user
        if (currentUser != null) {
            binding.headerName.text = currentUser.name
            binding.email.text = currentUser.email
            Glide.with(this).load(currentUser.userImage).into(binding.headerProfileImg)
        }
    }

    private fun getAppVersion(): String {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return "Unknown"
        }
    }
}