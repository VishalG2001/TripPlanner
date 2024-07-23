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
import com.example.recyclerviewapp.realmdb.RealmTrips
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity(), RVAdapter.OnTripItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tripAdapter: RVAdapter
    private lateinit var realm: Realm
    private var currentPosition = -1

    private val addTripResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            Realm.init(this)
            realm = Realm.getDefaultInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing Realm", Toast.LENGTH_SHORT).show()
        }

        setupRecyclerView()
        binding.fabNewTrip.setOnClickListener {
            currentPosition = -1
            val intent = Intent(this, CreateTripActivity::class.java)
            addTripResultLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        tripAdapter = RVAdapter(realm.where(RealmTrips::class.java).findAll(), this)
        binding.recyclerviewTrips.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewTrips.adapter = tripAdapter
    }

    private fun loadData() {
        // Load data from Realm and update the adapter
        val tripList = realm.where(RealmTrips::class.java).findAll()
        tripAdapter.updateData(tripList)
    }

    override fun onItemClick(trip: RealmTrips, position: Int) {
        currentPosition = position
        val intent = Intent(this, CreateTripActivity::class.java).apply {
            putExtra("from", trip.from)
            putExtra("to", trip.to)
            putExtra("status", trip.status)
            putExtra("distance", trip.distance)
            putExtra("duration", trip.duration)
            putExtra("date", trip.date)
            putExtra("stations", trip.stations)
            putExtra("tripId", trip.id)
            putExtra("title", "Edit Trip")
        }
        addTripResultLauncher.launch(intent)
    }

    override fun onItemLongClick(trip: RealmTrips, position: Int) {
        showDeleteConfirmationDialog(position)
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                realm.executeTransaction { realm ->
                    tripAdapter.getItemList().deleteFromRealm(position)
                }
                tripAdapter.notifyItemRemoved(position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
