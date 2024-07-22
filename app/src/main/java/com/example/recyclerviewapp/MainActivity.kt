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
    private lateinit var tripList: RealmResults<RealmTrips>
    private lateinit var realm: Realm
    private var currentPosition = -1

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

                realm.executeTransaction {
                    if (currentPosition == -1) {
                        val newTrip = realm.createObject(RealmTrips::class.java, UUID.randomUUID().toString())
                        newTrip.from = from
                        newTrip.to = to
                        newTrip.distance = distance
                        newTrip.duration = duration
                        newTrip.date = date
                        newTrip.stations = stations
                        newTrip.status = status.name
                        tripAdapter.notifyItemInserted(tripList.size -1)
                    } else {
                        val tripToUpdate = tripList[currentPosition]
                        tripToUpdate?.apply {
                            this.from = from
                            this.to = to
                            this.distance = distance
                            this.duration = duration
                            this.date = date
                            this.stations = stations
                            this.status = status.name
                            tripAdapter.notifyItemChanged(currentPosition)
                        }
                    }
                    currentPosition = -1
                    Toast.makeText(this, "${realm.isEmpty}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        tripList = realm.where(RealmTrips::class.java).findAll()
        tripAdapter = RVAdapter(tripList, this)

        binding.recyclerviewTrips.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewTrips.adapter = tripAdapter

        binding.fabNewTrip.setOnClickListener {
            currentPosition = -1
            val intent = Intent(this, CreateTripActivity::class.java)
            addTripResultLauncher.launch(intent)
        }
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
                realm.executeTransaction {
                    tripList.deleteFromRealm(position)
                }
                dialog.dismiss()
                tripAdapter.notifyItemRemoved(position)
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