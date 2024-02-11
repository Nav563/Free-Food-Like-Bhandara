package com.example.freefood_likebhandara.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freefood_likebhandara.activities.BhandaraDetailsActivity
import com.example.freefood_likebhandara.activities.ShareBhandaraLocationActivity
import com.example.freefood_likebhandara.adapter.RecentBhandaraAdapter
import com.example.freefood_likebhandara.databinding.FragmentDailyBhandaraBinding
import com.example.freefood_likebhandara.model.BhandaraModel
import com.example.freefood_likebhandara.viewmodel.DailyBhandaraViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import java.util.Locale


class DailyBhandaraFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentDailyBhandaraBinding
    private lateinit var adapter: RecentBhandaraAdapter
    private lateinit var listImages: MutableList<String>
    private var listItem = mutableListOf<BhandaraModel>()
    private lateinit var dailyBhandaraViewModel: DailyBhandaraViewModel
    val radius = 10.00 // replace with your desired radius in kilometers
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googlemap: GoogleMap
    private var lati: Double = 0.0
    private var longi: Double = 0.0
    private lateinit var currentAddString: String
    private var isMapReady = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val DEFAULT_ZOOM_LEVEL = 15f
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyBhandaraBinding.inflate(layoutInflater)
        listImages = mutableListOf()
        initializeRecyclerView()
        // Initialize ViewModel
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        dailyBhandaraViewModel = ViewModelProvider(this)[DailyBhandaraViewModel::class.java]
        // Observe changes in the LiveData
        dailyBhandaraViewModel.bhandaraListLiveData.observe(
            viewLifecycleOwner,
            Observer { bhandaraList ->
                // Update the UI with the new list of Bhandara
                adapter.updateItems(bhandaraList)
            })
        // Fetch data using ViewModel
        dailyBhandaraViewModel.fetchBhandaraData()
        return binding.root
    }


    private fun initializeRecyclerView() {
        adapter = RecentBhandaraAdapter(listItem) { selectedItem ->
            val intent = Intent(requireActivity(), BhandaraDetailsActivity::class.java)
            // intent.putExtra("selected_item", selectedItem)

            val gson = Gson()
            val json = gson.toJson(selectedItem)
            intent.putExtra("selected_item", json)

            startActivity(intent)
        }
        binding.dailyBhandaraRecycler.adapter = adapter
        binding.dailyBhandaraRecycler.layoutManager = LinearLayoutManager(requireContext())
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
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ShareBhandaraLocationActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
    }

    private fun getCurrentLocationAndZoomMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val list: MutableList<Address>? =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    binding.apply {
                        //binding.tvMapLocation.text = "${list?.get(0)?.getAddressLine(0)}"
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
        lati = mapLocation.latitude
        longi = mapLocation.longitude
    }


}