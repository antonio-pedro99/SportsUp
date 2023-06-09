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
    private val eventsCollection = Firebase.firestore.collection("games")

    private val _currentUser = Firebase.auth.currentUser
    private val _games = MutableLiveData<List<GameModel>?>().apply {

        gameRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(GameModel::class.java)
            }
            value = items
        }

    }


    private val eventsLiveData = MutableLiveData<List<GameEvent>>()

    private val _events = eventsLiveData.apply {
        eventsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) return@addSnapshotListener

            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(GameEvent::class.java)
                    .also { gameEvent -> gameEvent?.id = doc.id }
            }
            value = items?.filter { it.audience == "Everyone" && it.host_ref != _currentUser?.uid }
        }
    }

    fun getEvents(gameCategory: String) {
        eventsLiveData.apply {
            val query = eventsCollection.document(gameCategory.lowercase()).collection("items")

            query.addSnapshotListener { snapshot, exception ->
                if (exception != null) return@addSnapshotListener

                val items = snapshot?.documents?.mapNotNull { doc -> doc.toObject(GameEvent::class.java)
                    .also { gameEvent -> gameEvent?.id = doc.id } }
                value = items?.filter { it.audience =="Everyone" && it.host_ref != _currentUser?.uid}
            }
        }
    }

    val games: MutableLiveData<List<GameModel>?> = _games
    val events: MutableLiveData<List<GameEvent>> = _events
}