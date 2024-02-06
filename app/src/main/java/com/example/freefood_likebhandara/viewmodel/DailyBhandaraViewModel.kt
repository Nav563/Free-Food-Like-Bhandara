package com.example.freefood_likebhandara.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freefood_likebhandara.model.BhandaraModel
import com.example.freefood_likebhandara.repository.DailyBhandaraRepository
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class DailyBhandaraViewModel : ViewModel() {
    private val bhandaraRepository = DailyBhandaraRepository()
    val bhandaraListLiveData = MutableLiveData<List<BhandaraModel>>()

    fun fetchBhandaraData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDate = getStartDate() // Replace with your start date
                val endDate = getEndDate()
                val bhandaraList = bhandaraRepository.getBhandaraData(startDate, endDate)
                bhandaraListLiveData.postValue(bhandaraList)
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error fetching Bhandara data: ${e.message}")
            }
        }
    }

    private fun getStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        return calendar.time
    }

    private fun getEndDate(): Date {
        return Date()
    }


    companion object {
        const val TAG = "DailyBhandaraViewModel"
    }
}