package com.example.recyclerviewapp.realmdb

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class MyRealmMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersionMutable = oldVersion // Convert to mutable variable
        val schema = realm.schema

        if (oldVersionMutable == 0L) {
            schema.create("RealmTrips")
                .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("from", String::class.java)
                .addField("to", String::class.java)
                .addField("status", String::class.java)
                .addField("distance", String::class.java)
                .addField("duration", String::class.java)
                .addField("date", String::class.java)
                .addField("stations", String::class.java)
            oldVersionMutable++
        }
    }
}