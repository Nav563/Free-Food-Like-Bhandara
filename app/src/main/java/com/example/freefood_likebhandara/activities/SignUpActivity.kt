package com.example.freefood_likebhandara.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.freefood_likebhandara.MainActivity
import com.example.freefood_likebhandara.R
import com.example.freefood_likebhandara.databinding.ActivitySignUpBinding
import com.example.freefood_likebhandara.model.PostModel
import com.example.freefood_likebhandara.model.UserModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        binding.btnSignUp.setOnClickListener {
            validateUser()
        }
        binding.txtSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun validateUser() {
        if (binding.name.text!!.isEmpty() || binding.mobile.text!!.isEmpty() || binding.email.text!!.isEmpty())
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        else {
            createUserAccount()
        }
    }

    private fun createUserAccount(){
        val builder = AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Please wait")
            .setCancelable(false)
            .create()
        builder.show()

        firebaseAuth.createUserWithEmailAndPassword(binding.email.text.toString(),
            binding.password.text.toString())
            .addOnSuccessListener {
                //Account Created, now add user info in db
                updateUserInfo(it)
                saveUserInFireStore(it.user!!.uid)
                saveUserInRealTimeDatabase(it.user!!.uid)
                builder.dismiss()
                moveBackToSingInScreen()
            }
            .addOnFailureListener{ e ->
                // Failed creating account
                builder.dismiss()
                Toast.makeText(this, "Failed creating account ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun moveBackToSingInScreen() {
        finish()
    }

    private fun updateUserInfo(authResult: AuthResult) {

        var builder = AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Please wait")
            .setCancelable(false)
            .create()

        val profile = UserProfileChangeRequest.Builder()
        profile.displayName =binding.name.text.toString()
        authResult.user?.updateProfile(profile.build())

    }

    private fun saveUserInFireStore(uid: String) {
        val data = UserModel(name = binding.name.text.toString(), mobile = binding.mobile.text.toString(),
            email = binding.email.text.toString(), password = binding.password.text.toString())

        Firebase.firestore.collection("Users").document(uid)
            .set(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Successfully Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e ->
                e.message
            }
    }
    private fun saveUserInRealTimeDatabase(uid: String) {
        val data = UserModel(name = binding.name.text.toString(), mobile = binding.mobile.text.toString(),
            email = binding.email.text.toString(), password = binding.password.text.toString())
        databaseReference.child("Users").child(uid).setValue(data)
    }

}