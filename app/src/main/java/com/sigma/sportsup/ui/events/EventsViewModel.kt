package com.sigma.sportsup.ui.events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel

class EventsViewModel : ViewModel() {

    private val gameRef = FirestoreCollection().games

    private val _games = MutableLiveData<List<GameModel>?>().apply {

        gameRef.addSnapshotListener { snapshot, exception ->
            if (exception!=null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot -> documentSnapshot.toObject(GameModel::class.java) }
            value = items
        }

    }

   // private val _events = MutableLiveData<List<GameModel>?> = _

    val games : MutableLiveData<List<GameModel>?> = _games
}