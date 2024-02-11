package com.example.freefood_likebhandara.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.freefood_likebhandara.activities.AddChefActivity
import com.example.freefood_likebhandara.activities.BookChefActivity
import com.example.freefood_likebhandara.activities.EditProfileActivity
import com.example.freefood_likebhandara.activities.ShareBhandaraLocationActivity
import com.example.freefood_likebhandara.adapter.ChefAdapter
import com.example.freefood_likebhandara.adapter.ImagePagerAdapter
import com.example.freefood_likebhandara.databinding.FragmentHomeBinding
import com.example.freefood_likebhandara.databinding.ScrollingHomeFragmentContentBinding
import com.example.freefood_likebhandara.model.Chef
import com.example.freefood_likebhandara.model.PostModel
import com.example.freefood_likebhandara.model.UserModel
import com.example.freefood_likebhandara.viewmodel.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sliderBinding: ScrollingHomeFragmentContentBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var userViewModel: UserViewModel

    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        // Observe changes in the user list and update the UI
        userViewModel.currentUserLiveData.observe(requireActivity(), Observer { users ->
            // Update UI with the list of users
            updateUI(users)
        })
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // When the user submits the search query, start the SearchResultsActivity
                if (query != null && query.isNotBlank()) {
                    val intent = Intent(requireContext(), BookChefActivity::class.java)
                    intent.putExtra("SEARCH_QUERY", query)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes if needed
                return true
            }
        })
        // viewPager =

        // Trigger the ViewModel to fetch user data
        userViewModel.fetchCurrentUsers()

        sliderBinding = ScrollingHomeFragmentContentBinding.inflate(layoutInflater)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.imgProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        binding.shareBhandaraLocation.setOnClickListener {
            startActivity(Intent(requireContext(), ShareBhandaraLocationActivity::class.java))
        }
        binding.freeFood.setOnClickListener {
            startActivity(Intent(requireContext(), AddChefActivity::class.java))
        }
        binding.bookChef.setOnClickListener {
            startActivity(Intent(requireContext(), BookChefActivity::class.java))
        }
        getLocation()
        //getProductDetails()

        return binding.root
    }

    private fun updateUI(currentUser: UserModel?) {
        // Update UI elements with the list of user
        if (currentUser != null) {
            Glide.with(this).load(currentUser.userImage).into(binding.imgProfile)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        binding.apply {
                            binding.currentLocation.text = "${list?.get(0)?.getAddressLine(0)}"
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), permissionId
        )
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
//    private fun getProductDetails() {
//
//        Firebase.firestore.collection("Users")
//            .document().get().addOnSuccessListener {
//                val list = it.get("userImage") as ArrayList<String>
//
//                val slideList = ArrayList<UserModel>()
//                for (data in list){
//                    slideList.add(UserModel(data))
//                }
//
//                //binding.imageSlider.setImageList(slideList, ScaleTypes.CENTER_CROP)
//                //binding.imageSlider.setImageList(slideList)
//
//            }
//            .addOnFailureListener{
////                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//            }
//    }
}