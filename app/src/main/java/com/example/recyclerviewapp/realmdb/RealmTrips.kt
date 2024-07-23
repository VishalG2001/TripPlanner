package com.example.recyclerviewapp.realmdb

import com.example.recyclerviewapp.TripStatus
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RealmTrips(
    @PrimaryKey var id: String = "",
    @Required var from: String = "",
    @Required var to: String = "",
    @Required var status: String = TripStatus.SUCCESSFUL.name,
    @Required var distance: String = "",
    @Required var duration: String = "",
    @Required var date: String = "",
    @Required var stations: String = ""
) : RealmObject()
