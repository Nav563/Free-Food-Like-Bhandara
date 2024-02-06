package com.example.freefood_likebhandara.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.freefood_likebhandara.databinding.ActivityAddChefBinding
import com.example.freefood_likebhandara.model.Chef
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date
import java.util.UUID

class AddChefActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddChefBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var chefimg: Uri? = null
    private lateinit var progressDialog: ProgressDialog
    private var chefImgUrl: String? = ""
    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            chefimg = it.data!!.data
            binding.chefImg.setImageURI(chefimg)
            binding.chefImg.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChefBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.chefImg.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }
        binding.addChef.setOnClickListener {
            validateChefData()
        }

    }

    private fun validateChefData() {
        if (binding.name.text.isEmpty()  || binding.mobile.text.isEmpty()
            || binding.email.text.isEmpty() || binding.address.text.isEmpty()
        ) {
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()

        } else if (chefimg != null){
            uploadImage()
        }else{
            Toast.makeText(this, "Please select Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        progressDialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("Chefs/$fileName")
        refStorage.putFile(chefimg!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    chefImgUrl = image.toString()
                    storeChefData()
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeChefData() {
        progressDialog.show()
        val currentDate = Date()
        val timeStamp = Timestamp(currentDate)
        val data = Chef(
            chefImage = chefImgUrl.toString(),
            chefName = binding.name.text.toString(),
            chefMobile = binding.mobile.text.toString(),
            chefEmail = binding.email.text.toString(),
            chefAddress = binding.address.text.toString(),
            timestamp = timeStamp,

            )
        Firebase.firestore.collection("Chefs").document()
            .set(data)
            .addOnSuccessListener {
                val intent = Intent(this, AddChefActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }
}