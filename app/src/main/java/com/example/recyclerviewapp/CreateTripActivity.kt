package com.example.recyclerviewapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerviewapp.databinding.ActivityCreateTripBinding
import com.example.recyclerviewapp.realmdb.RealmTrips
import io.realm.Realm
import java.util.Calendar
import java.util.UUID

class CreateTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTripBinding
    private var selectedYear = -1
    private var selectedMonth = -1
    private var selectedDay = -1
    private lateinit var realm: Realm
    private var editid:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            Realm.init(this)
            realm = Realm.getDefaultInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing Realm", Toast.LENGTH_SHORT).show()
        }

        editid = intent.getStringExtra("tripId")
        if (editid != null) {
            // Load trip details and populate fields
            val trip = realm.where(RealmTrips::class.java).equalTo("id", editid).findFirst()
            trip?.let {
                binding.etFrom.setText(it.from)
                binding.etTo.setText(it.to)
                binding.etdistance.setText(it.distance)
                binding.etTime.setText(it.duration)
                binding.etDate.setText(it.date)
                binding.etStations.setText(it.stations)
            }
        }

        binding.btnsubmit.setOnClickListener {

            binding.ftfStartLocation.error = null
            binding.ftfEndLocation.error = null
            binding.ftfStations.error = null
            binding.ftfDate.error = null
            binding.ftfTime.error = null
            binding.ftfDistance.error = null
            binding.etFrom.addValidation()
            binding.etTo.addValidation()
            binding.etdistance.addValidation()
            binding.etTime.addValidation()
            binding.etDate.addValidation()
            binding.etStations.addValidation()

            val fromLocation = binding.etFrom.text.toString()
            val toLocation = binding.etTo.text.toString()
            val distanceTravel = binding.etdistance.text.toString()
            val timeTravel = binding.etTime.text.toString()
            val dateTravel = binding.etDate.text.toString()
            val stationTravel = binding.etStations.text.toString()
            val status = intent.getStringExtra("status") ?: TripStatus.SUCCESSFUL.name

            if (fromLocation.trim().isEmpty()) {
                binding.ftfStartLocation.error = "This field is required"
                binding.etFrom.requestFocus()
                showKeyboard(binding.etFrom)
            } else if (toLocation.trim().isEmpty()) {
                binding.ftfEndLocation.error = "This field is required"
                binding.etTo.requestFocus()
                showKeyboard(binding.etTo)
                binding.ftfStartLocation.error = null
            } else if (distanceTravel.trim().isEmpty()) {
                binding.ftfDistance.error = "This field is required"
                binding.etdistance.requestFocus()
                showKeyboard(binding.etdistance)
                binding.ftfEndLocation.error = null
            } else if (dateTravel.trim().isEmpty()) {
                binding.ftfDate.error = "This field is required"
                binding.etDate.requestFocus()
                showKeyboard(binding.etDate)
            } else if (timeTravel.trim().isEmpty()) {
                binding.ftfTime.error = "This field is required"
                binding.etTime.requestFocus()
                showKeyboard(binding.etTime)
            } else if (stationTravel.trim().isEmpty()) {
                binding.ftfStations.error = "This field is required"
                binding.etStations.requestFocus()
                showKeyboard(binding.etStations)
            } else {
                if (editid != null) {
                updateTrip()
            } else {
                saveTrip()
            }
            }
        }
    }
    private fun saveTrip() {
        val fromLocation = binding.etFrom.text.toString()
        val toLocation = binding.etTo.text.toString()
        val distanceTravel = binding.etdistance.text.toString()
        val timeTravel = binding.etTime.text.toString()
        val dateTravel = binding.etDate.text.toString()
        val stationTravel = binding.etStations.text.toString()
        val status = intent.getStringExtra("status") ?: TripStatus.SUCCESSFUL.name

        realm.executeTransaction { realm ->
            val trip = realm.createObject(
            RealmTrips::class.java, UUID.randomUUID().toString())
            trip.from = fromLocation
            trip.to = toLocation
            trip.distance = distanceTravel
            trip.duration = timeTravel
            trip.date = dateTravel
            trip.stations = stationTravel
            trip.status = status
        }

        setResult(RESULT_OK)
        finish()
    }
    private fun updateTrip() {
        val fromLocation = binding.etFrom.text.toString()
        val toLocation = binding.etTo.text.toString()
        val distanceTravel = binding.etdistance.text.toString()
        val timeTravel = binding.etTime.text.toString()
        val dateTravel = binding.etDate.text.toString()
        val stationTravel = binding.etStations.text.toString()
        val status = intent.getStringExtra("status") ?: TripStatus.SUCCESSFUL.name

        realm.executeTransaction { realm ->
            val trip = realm.where(RealmTrips::class.java).equalTo("id", editid).findFirst()
            trip?.let {
                it.from = fromLocation
                it.to = toLocation
                it.distance = distanceTravel
                it.duration = timeTravel
                it.date = dateTravel
                it.stations = stationTravel
                it.status = status
            }
        }

        setResult(RESULT_OK)
        finish()
    }

    fun showDatePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        if (selectedYear != -1 && selectedMonth != -1 && selectedDay != -1) {
            calendar.set(selectedYear, selectedMonth, selectedDay)
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            view.context,
            { _, year, monthOfYear, dayOfMonth ->
                selectedYear = year
                selectedMonth = monthOfYear
                selectedDay = dayOfMonth
                val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                binding.etDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    fun showTimePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = "$hourOfDay:$minute"
                binding.etTime.setText(selectedTime)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun EditText.addValidation() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@addValidation.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    this@addValidation.error = "This field is required"
                } else {
                    this@addValidation.error = null
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::realm.isInitialized) {
            realm.close()
        }
    }
}