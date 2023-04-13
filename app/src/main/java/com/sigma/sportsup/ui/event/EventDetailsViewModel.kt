package com.sigma.sportsup.ui.event

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameEvent

class EventDetailsViewModel : ViewModel() {

    private val eventsRef = Firebase.firestore.collection("games")

    private val eventDetailsLiveData = MutableLiveData<GameEvent>()

    fun getEventById(documentId: String, eventName: String) {
        eventDetailsLiveData.apply {
            val query = eventsRef.document(eventName.lowercase()).collection("items").whereEqualTo(
                FieldPath.documentId(), documentId
            )

            query.addSnapshotListener { q, _ ->
                for (doc in q?.documents!!) {
                    value = doc.toObject(GameEvent::class.java)
                }
            }
        }
    }

    val eventDetails = eventDetailsLiveData
}