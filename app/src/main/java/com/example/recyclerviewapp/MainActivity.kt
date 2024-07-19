package com.example.recyclerviewapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), RVAdapter.OnTripItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tripAdapter: RVAdapter
    private lateinit var tripList: ArrayList<Trip>
    var currentPosition = -1

    private val addTripResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val from = data.getStringExtra("from") ?: ""
                val to = data.getStringExtra("to") ?: ""
                val distance = data.getStringExtra("distance") ?: ""
                val duration = data.getStringExtra("duration") ?: ""
                val date = data.getStringExtra("date") ?: ""
                val stations = data.getStringExtra("stations") ?: ""
                val status = TripStatus.valueOf(data.getStringExtra("status") ?: TripStatus.SUCCESSFUL.name)

                val newTrip = Trip(from, to, status, distance, duration, date, stations)

                if (currentPosition == -1) {
                    tripList.add(newTrip)
                    tripAdapter.notifyItemInserted(tripList.size - 1)
                    binding.recyclerviewTrips.scrollToPosition(currentPosition)

//                    tripAdapter.notifyDataSetChanged()
                } else {
                    tripList[currentPosition] = newTrip
                    tripAdapter.notifyItemChanged(currentPosition)
                    currentPosition = -1
                    binding.recyclerviewTrips.scrollToPosition(tripList.size - 1)

                }
//                Toast.makeText(this, "${tripList.size}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripList = arrayListOf()

        tripAdapter = RVAdapter(tripList, this)

        binding.recyclerviewTrips.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewTrips.adapter = tripAdapter

        binding.fabNewTrip.setOnClickListener {
            currentPosition = -1
            val intent = Intent(this, CreateTripActivity::class.java)
            addTripResultLauncher.launch(intent)
        }
    }

    override fun onItemClick(trip: Trip, position: Int) {
        currentPosition = position
        val intent = Intent(this, CreateTripActivity::class.java).apply {
            putExtra("from", trip.from)
            putExtra("to", trip.to)
            putExtra("status", trip.status.name)
            putExtra("distance", trip.distance)
            putExtra("duration", trip.duration)
            putExtra("date", trip.date)
            putExtra("stations", trip.stations)
        }
        intent.putExtra("title", "Edit Trip")
        addTripResultLauncher.launch(intent)

    }

    override fun onItemLongClick(trip: Trip, position: Int) {
        showDeleteConfirmationDialog(position)
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                tripList.removeAt(position)
                tripAdapter.notifyItemRemoved(position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
