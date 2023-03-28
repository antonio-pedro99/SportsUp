package com.sigma.sportsup.ui.events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.data.UserModel

class EventsViewModel : ViewModel() {

    private val gameRef = FirestoreCollection().games
    private val eventsRef = FirestoreCollection().events

    private val _games = MutableLiveData<List<GameModel>?>().apply {

        gameRef.addSnapshotListener { snapshot, exception ->
            if (exception!=null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot -> documentSnapshot.toObject(GameModel::class.java) }
            value = items
        }

    }


   private val _events = MutableLiveData<List<GameEvent>>().apply {
       eventsRef.addSnapshotListener { snapshot, exception ->
           if (exception != null) return@addSnapshotListener

           val items = snapshot?.documents?.mapNotNull { doc -> doc.toObject(GameEvent::class.java) }
           value = items
       }
   }

    val games : MutableLiveData<List<GameModel>?> = _games
    val events: MutableLiveData<List<GameEvent>> = _events
}