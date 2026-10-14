package com.example.radiohi

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore(name = "stations")

class StationRepository(private val context: Context) {

    private val json = Json{ignoreUnknownKeys = true}
    private val stationsKey = stringPreferencesKey("saved_stations")

    val savedStations: Flow<List<Station>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[stationsKey] ?: ""
            if (jsonString.isEmpty()) emptyList()
            else json.decodeFromString<List<Station>>(jsonString)
        }
    suspend fun saveStations(stations: List<Station>){
        val jsonString = json.encodeToString(stations)
        context.dataStore.edit{
            preferences -> preferences[stationsKey] = jsonString
        }
    }
}