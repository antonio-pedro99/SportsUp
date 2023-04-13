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
import com.sigma.sportsup.data.UserModel

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

    fun rsvp(documentId: String, eventName: String, user: UserModel) {
        val query = eventsRef.document(eventName.lowercase()).collection("items")
            .whereEqualTo(FieldPath.documentId(), documentId)
        query.get().addOnSuccessListener {
            for (doc in it){
                val toAdd = if (doc.data["waiting"] == null) 1 else doc.data["waiting"].toString().toInt() + 1
                doc.reference.update("waiting", toAdd)
                doc.reference.collection("waiting_room").add(hashMapOf(
                    "waiter_id" to user.id,
                    "waiter_name" to user.name
                )).addOnSuccessListener {
                    Log.d("S", "Success")
                }.addOnFailureListener {ex->
                    Log.d("F", ex.message.toString())
                }
            }
        }.addOnFailureListener {
            Log.d("F", it.message.toString())
        }
    }

    val eventDetails = eventDetailsLiveData
}