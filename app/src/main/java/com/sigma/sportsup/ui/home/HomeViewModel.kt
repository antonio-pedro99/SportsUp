package com.sigma.sportsup.ui.home

import android.os.Build
import android.se.omapi.Session
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.data.SessionEvent
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val gameRef = FirestoreCollection().games
    private val eventRef = FirestoreCollection().events

    private val _games = MutableLiveData<List<GameModel>?>().apply {
        gameRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot -> documentSnapshot.toObject(GameModel::class.java) }
            value = items
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val _sessions = MutableLiveData<List<GameEvent>>().apply {

        eventRef.addSnapshotListener { snapshot, exception ->
            val currentDate = DateTimeFormatter
                .ofPattern("dd/MM/yyyy", Locale.getDefault())
                .format(LocalDate.now())
            if (exception != null) return@addSnapshotListener


            Log.d("F", snapshot?.documents.toString())
            val items = snapshot?.documents?.mapNotNull { doc-> doc.toObject(GameEvent::class.java)}
            value = items?.filter { it.date == currentDate }
        }
    }

    val games: LiveData<List<GameModel>?> = _games
    @RequiresApi(Build.VERSION_CODES.O)
    val sessions:LiveData<List<GameEvent>> = _sessions


}