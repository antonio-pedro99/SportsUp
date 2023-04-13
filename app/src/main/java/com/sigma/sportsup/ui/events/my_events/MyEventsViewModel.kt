package com.sigma.sportsup.ui.events

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel

class MyEventsViewModel : ViewModel() {

    private val gameRef = FirestoreCollection().games
    private val eventsRef = FirestoreCollection().events
    private val eventsCollection = Firebase.firestore.collection("games")

    private val _currentUser = Firebase.auth.currentUser

    private val eventsLiveData = MutableLiveData<List<GameEvent>>()

    private val _events = eventsLiveData.apply {
        eventsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) return@addSnapshotListener

            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(GameEvent::class.java)
                    .also { gameEvent -> gameEvent?.id = doc.id }
            }
            value = items?.filter { it.host_ref == _currentUser?.uid }
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

    val events: MutableLiveData<List<GameEvent>> = _events
}