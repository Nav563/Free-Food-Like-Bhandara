package com.example.freefood_likebhandara.activities

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.freefood_likebhandara.databinding.ActivityShareBhandaraLocationBinding
import com.example.freefood_likebhandara.model.BhandaraModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ShareBhandaraLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityShareBhandaraLocationBinding
    private lateinit var googlemap: GoogleMap
    private var isMapReady = false
    private var bhandaraImgUrl: String? = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var selectedDay: String
    private val calendar = Calendar.getInstance()
    private lateinit var currentAddString: String
    private var selectedLocation: Double = 0.0
    private var lati: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBhandaraLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false) // Optional: Set whether the dialog can be canceled by clicking outside of it
        //progressDialog.show()
        setupSpinner()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.imgCapture.setOnClickListener {
            cameraPermission()
        }
        binding.shareLocation.setOnClickListener {
            validateBhandaraData()
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googlemap = googleMap
        isMapReady = true
        if (checkLocationPermission()) {
            getCurrentLocationAndZoomMap()
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
    }

    private fun getCurrentLocationAndZoomMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Create a LatLng object from the current location
                    val currentLatLng = LatLng(it.latitude, it.longitude)

                    // Zoom map to current location
                    zoomMapToLocation(currentLatLng)
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val list: MutableList<Address>? =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    binding.apply {
                        binding.tvMapLocation.text = "${list?.get(0)?.getAddressLine(0)}"
                        currentAddString = "${list?.get(0)?.getAddressLine(0)}"
                    }
                    googlemap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            15.0f
                        )
                    )

                     //Store the current location in Firestore
                    val currentMapLocation = BhandaraModel(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        zoom = googlemap.cameraPosition.zoom
                    )
                    addLocationToFirestore(currentMapLocation)
                }
            }
            .addOnFailureListener { e ->
                // Handle failure to obtain location
                e.printStackTrace()
            }
    }

    private fun zoomMapToLocation(location: LatLng) {
        // Zoom the map to the current location
        googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM_LEVEL))
        // Add a marker at the current location
        googlemap.addMarker(MarkerOptions().position(location).title("Current Location"))
    }

    private fun addLocationToFirestore(mapLocation: BhandaraModel) {
        selectedLocation = mapLocation.latitude
        lati = mapLocation.longitude
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val DEFAULT_ZOOM_LEVEL = 15f
    }

    /*
        Setup Spinner
     */
    private fun setupSpinner() {
        val data =
            arrayOf("All Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.weekSpinner.adapter = adapter
        binding.weekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = data[position]
                selectedDay = selectedOption
                Toast.makeText(applicationContext, "Selected: $selectedOption", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    /*
        For Handle DatePicker
     */
    fun showDatePickerDialog(view: View) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Update the Calendar and TextView with the selected date
                calendar.set(selectedYear, selectedMonth, selectedDay)
                updateDateTextView()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateDateTextView() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        binding.edtBhandaraDate.text = formattedDate
    }

    /*
         For Open Camera
     */
    private fun cameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(permission, 112)
            openCamera()
        } else {
            Toast.makeText(this, "Please Allow Camera to Click Picture", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var imageUri: Uri
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private val cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val inputImage: Bitmap? = uriToBitmap(imageUri)
                val rotated: Bitmap = rotateBitmap(inputImage!!)
                binding.imgViewCamera.setImageBitmap(rotated)
            }
        }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        return try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            image
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("Range")
    private fun rotateBitmap(input: Bitmap): Bitmap {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur: Cursor? = contentResolver.query(imageUri, orientationColumn, null, null, null)
        var orientation = -1
        cur?.use {
            if (it.moveToFirst()) {
                orientation = it.getInt(it.getColumnIndex(orientationColumn[0]))
            }
        }
        Log.d("tryOrientation", orientation.toString())
        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(orientation.toFloat())
        return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
    }

    /*
        Validate Data to Store in FireStore
     */
    private fun validateBhandaraData() {
        if (binding.edtBhandaraDate.text.isEmpty() ||
            binding.edtBhandaraAddress.text.isEmpty() || imageUri.equals(null)
        ) {
            Toast.makeText(this, "Please Select Bhandara Image", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }
    }

    /*
        To Upload Image in FireStorage
     */
    private fun uploadImage() {
        //val fileName = UUID.randomUUID().toString()+".jpg"
        progressDialog.show()
        val fileName = imageUri.toString()
        val refStorage = FirebaseStorage.getInstance().reference.child("Bhandara/$fileName")
        refStorage.putFile(imageUri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    bhandaraImgUrl = image.toString()
                    storeData()
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    /*
        To Store Data in FireStore
     */
    private fun storeData() {
        val currentdate = Date()
        val a = FirebaseAuth.getInstance().currentUser?.displayName
        val b = FirebaseAuth.getInstance().currentUser?.uid
        val timestamp = Timestamp(currentdate)
        val data = BhandaraModel(
            bhandaraImage = bhandaraImgUrl.toString(),
            bhandaraAddress = binding.edtBhandaraAddress.text.toString(),
            bhandaraLocation = binding.tvMapLocation.text.toString(),
            bhandaraDate = binding.edtBhandaraDate.text.toString(),
            postDate = timestamp.toString(),
            postedByID = b.toString(),
            postedByName = a.toString(),
            bhandaraDay = selectedDay,
            startTime = binding.startTime.text.toString(),
            endTime = binding.endTime.text.toString(),
            timestamp = timestamp,
            latitude = selectedLocation,
            longitude = lati
        )
        Firebase.firestore.collection("Bhandara").document()
            .set(data)
            .addOnSuccessListener {
                val intent = Intent(this, ShareBhandaraLocationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }
    /*
        To Show Start TimePicker And Select time to Store in FireStore
     */
    fun showStartTimePickerDialog(view: View) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Update the Calendar and TextView with the selected time
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                updateStartTimeTextView()
            },
            hour,
            minute,
            false // 24-hour format (change to true for 24-hour format)
        )
        // Show the TimePickerDialog
        timePickerDialog.show()
    }

    private fun updateStartTimeTextView() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)
        binding.startTime.text = formattedTime
    }

    /*
        To Show End TimePicker And Select time to Store in FireStore
     */
    fun showEndTimePickerDialog(view: View) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Update the Calendar and TextView with the selected time
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                updateEndTimeTextView()
            },
            hour,
            minute,
            false // 24-hour format (change to true for 24-hour format)
        )
        // Show the TimePickerDialog
        timePickerDialog.show()
    }

    private fun updateEndTimeTextView() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)
        binding.endTime.text = formattedTime
    }
}