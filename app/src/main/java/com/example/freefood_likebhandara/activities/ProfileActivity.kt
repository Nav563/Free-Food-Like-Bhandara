package com.example.freefood_likebhandara.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.freefood_likebhandara.R
import com.example.freefood_likebhandara.databinding.ActivityProfileBinding
import com.example.freefood_likebhandara.model.UserModel
import com.example.freefood_likebhandara.viewmodel.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Locale
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    private lateinit var edtname: EditText
    private lateinit var edtmobile: EditText
    private lateinit var camerabtn: CircleImageView
    private lateinit var save: Button
    private lateinit var cancelle: TextView
    private lateinit var imageView: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private var profileImg: Uri? = null
    private var profileImgUrl: String? = ""
    private lateinit var userViewModel: UserViewModel

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            profileImg = it.data!!.data
            imageView.setImageURI(profileImg)
            imageView.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        // Observe changes in the user list and update the UI
        userViewModel.currentUserLiveData.observe(this, Observer { users ->
            // Update UI with the list of users
            updateUI(users)
        })
        // Trigger the ViewModel to fetch user data
        userViewModel.fetchCurrentUsers()

        //binding.progressBarView.max = 100
        getLocation()

        binding.editProfile.setOnClickListener {
            openDialog()
        }
        binding.logoutTv.setOnClickListener {
            logOut()
        }
        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
        fetchUserDataAndUpdateProgressBar()
    }

    private fun updateUI(currentUser: UserModel?) {
        // Update UI elements with the list of user
        if (currentUser != null) {
            binding.userName.text = currentUser.name
            binding.mobile.text = currentUser.mobile
            binding.userEmail.text = firebaseAuth.currentUser?.email
            binding.txtPunyaCoins.text = currentUser.punyaCoins.toString()
            Glide.with(this).load(currentUser.userImage).into(binding.userImg)
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
    private fun fetchUserDataAndUpdateProgressBar() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_view)

        // Replace 'users' with your Firestore collection name
        // Assuming you have a document with the user's UID as the document ID

        firebaseAuth.currentUser?.uid?.let {
            Firebase.firestore.collection("Users")
                .document(it)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Convert Firestore document to UserModel
                        val userModel = documentSnapshot.toObject(UserModel::class.java)

                        // Update ProgressBar based on punyaCoins
                        userModel?.punyaCoins?.let { punyaCoins ->
                            // Assuming 100 is the maximum value for punyaCoins
                            val progress = (punyaCoins * 100) / 100  // Adjust 100 based on your maximum punyaCoins value

                            // Update ProgressBar
                            progressBar.progress = progress
                        }
                    } else {
                        // Document does not exist
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failures
                }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        binding.apply {
                            binding.userLocation.text =
                                "Address\n${list?.get(0)?.getAddressLine(0)}"
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun openDialog() {
        builder = AlertDialog.Builder(this)
        val itemView: View =
            LayoutInflater.from(applicationContext).inflate(R.layout.edit_profile_dialog, null)
        dialog = builder.create()
        dialog.setView(itemView)
        edtname = itemView.findViewById(R.id.edt_name)
        edtmobile = itemView.findViewById(R.id.edt_mobile)
        imageView = itemView.findViewById(R.id.img_profile)
        save = itemView.findViewById(R.id.update_userprofile)
        cancelle = itemView.findViewById(R.id.txt_cancel)
        camerabtn = itemView.findViewById<CircleImageView>(R.id.camera)

        camerabtn.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        save.setOnClickListener {
            validateUser()
        }
        dialog.show()
        cancelle.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun validateUser() {
        if (edtname.text.isEmpty() || edtmobile.text.isEmpty() || profileImg == null)
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        else
            uploadImage()
    }

    private fun uploadImage() {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage =
            FirebaseStorage.getInstance().reference.child("Users_Profile_Image/$fileName")
        refStorage.putFile(profileImg!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    profileImgUrl = image.toString()
                    storeData()
                    firebaseAuth.currentUser?.uid?.let { it1 -> saveUserInRealTimeDatabase(it1) }
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Please wait")
            .setCancelable(false)
            .create()
        builder.show()

        val map = hashMapOf<String, Any>()
        map["name"] = edtname.text.toString()
        map["mobile"] = edtmobile.text.toString()
        map["userImage"] = profileImgUrl.toString()
        firebaseAuth.currentUser?.uid?.let {
            Firebase.firestore
                .collection("Users").document(it)
                .set(map, SetOptions.merge()).addOnSuccessListener {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                    builder.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    builder.dismiss()
                }
        }
    }

    private fun saveUserInRealTimeDatabase(uid: String) {
        val data = UserModel(name = edtname.text.toString(), mobile = edtmobile.text.toString())
        databaseReference.child("Users").child(uid).setValue(data)
    }
}