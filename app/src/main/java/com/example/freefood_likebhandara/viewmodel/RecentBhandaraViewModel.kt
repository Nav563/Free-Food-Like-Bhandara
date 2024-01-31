package com.example.freefood_likebhandara.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freefood_likebhandara.model.BhandaraModel
import com.example.freefood_likebhandara.repository.BhandaraRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class RecentBhandaraViewModel : ViewModel() {
    private val bhandaraRepository = BhandaraRepository()

    val recentBhandaraLiveData = MutableLiveData<List<BhandaraModel>>()

    fun fetchLastThirtyDaysBhandaraData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDate = getStartDate() // Replace with your start date
                val endDate = getEndDate()
                val bhandaraList = bhandaraRepository.getLastThirtyDaysBhandara(startDate, endDate)
                recentBhandaraLiveData.postValue(bhandaraList)
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error fetching Bhandara data: ${e.message}")
            }
        }
    }

    private fun getStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -10)
        return calendar.time
    }

    private fun getEndDate(): Date {
        return Date()
    }

    companion object {
        const val TAG = "BhandaraViewModel"
    }
}