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
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerviewapp.databinding.ActivityCreateTripBinding
import java.util.Calendar

class CreateTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTripBinding
    private var selectedYear = -1
    private var selectedMonth = -1
    private var selectedDay = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            val testtb = it.getStringExtra("title")
            if (testtb != null) {
                binding.tbCreateTrip.setTitle(testtb)
            }

            binding.etFrom.setText(it.getStringExtra("from"))
            binding.etTo.setText(it.getStringExtra("to"))
            binding.etdistance.setText(it.getStringExtra("distance"))
            binding.etTime.setText(it.getStringExtra("duration"))
            binding.etDate.setText(it.getStringExtra("date"))
            binding.etStations.setText(it.getStringExtra("stations"))
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
                val resultIntent = Intent().apply {
                    putExtra("from", fromLocation)
                    putExtra("to", toLocation)
                    putExtra("distance", distanceTravel)
                    putExtra("duration", timeTravel)
                    putExtra("date", dateTravel)
                    putExtra("stations", stationTravel)
                    putExtra("status", status)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
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
}
