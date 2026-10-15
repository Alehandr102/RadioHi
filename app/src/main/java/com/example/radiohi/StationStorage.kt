package com.example.radiohi

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.stationsDataStore by preferencesDataStore(name = "stations")

object StationStorage {

    private val json = Json { ignoreUnknownKeys = true }
    private val key = stringPreferencesKey("saved_stations")

    suspend fun load(context: Context): List<Station> {
        return try {
            val jsonString = context.stationsDataStore.data.first()[key] ?: ""
            if (jsonString.isEmpty()) emptyList()
            else json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun save(context: Context, stations: List<Station>) {
        try {
            val jsonString = json.encodeToString(stations)
            context.stationsDataStore.edit { it[key] = jsonString }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}