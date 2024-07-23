package com.example.recyclerviewapp.realmdb

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .schemaVersion(2)
            .deleteRealmIfMigrationNeeded()
//            .migration(MyRealmMigration)
            .allowWritesOnUiThread(true)
            .modules(Realm.getDefaultModule(), RealmTripsModule())
            .build()
        Realm.setDefaultConfiguration(config)
    }
}
