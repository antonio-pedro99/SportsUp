package com.sigma.sportsup.ui.event

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.UserModel
import com.sigma.sportsup.data.WaiterUserModel

class EventDetailsViewModel : ViewModel() {

    private val eventsRef = Firebase.firestore.collection("games")

    private val eventDetailsLiveData = MutableLiveData<GameEvent>()
    private val rsvpAlreadySentLiveData = MutableLiveData<Boolean>()
    private val playerConfirmedLiveData = MutableLiveData<Boolean>()
    private val playersLiveData = MutableLiveData<List<UserModel>>()
    private val _gameWaitingListLiveData = MutableLiveData<List<WaiterUserModel>>()


    fun getEventById(documentId: String, eventName: String) {
        eventDetailsLiveData.apply {
            val query = getQuery(documentId, eventName)
            query.addSnapshotListener { q, _ ->
                for (doc in q?.documents!!) {
                    value = doc.toObject(GameEvent::class.java).also { it?.id = doc.id }
                }
            }
        }
    }

    fun checkRsvp(documentId: String, eventName: String, user: UserModel) {
        rsvpAlreadySentLiveData.apply {
            val query = getQuery(documentId, eventName)
            query.get().addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.collection("waiting_room").whereEqualTo(
                        "id",
                        user.id
                    ).get().addOnSuccessListener { querySnapshot ->
                        value = !querySnapshot.isEmpty || querySnapshot == null
                    }
                }
            }.addOnFailureListener {
                value = false
            }
        }
    }

    fun checkParticipation(documentId: String, eventName: String, user: UserModel) {
        playerConfirmedLiveData.apply {
            val query = getQuery(documentId, eventName)
            query.get().addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.collection("players").whereEqualTo(
                        "id",
                        user.id
                    ).get().addOnSuccessListener { querySnapshot ->
                        value = !querySnapshot.isEmpty || querySnapshot == null
                    }
                }
            }.addOnFailureListener {
                value = false
            }
        }
    }

    fun getGamePlayers(documentId: String, eventName: String) {
        playersLiveData.apply {
            val query = getQuery(documentId, eventName)
            query.get().addOnSuccessListener {

                for (doc in it) {
                    doc.reference.collection("players").addSnapshotListener { snapshot, _ ->
                        value = snapshot?.documents?.mapNotNull { document ->
                            document.toObject(UserModel::class.java)
                        }
                        Log.d("T", snapshot?.documents.toString())
                    }
                }
            }.addOnFailureListener {
                Log.d("F1", it.message.toString())
            }
        }
    }

    fun getWaitingList(documentId: String, eventName: String) {
        _gameWaitingListLiveData.apply {
            val query = getQuery(documentId, eventName)
            query.get().addOnSuccessListener {
                for (doc in it) {
                    doc.reference.collection("waiting_room").addSnapshotListener { snapshot, _ ->
                        value = snapshot?.documents?.mapNotNull { document ->
                            document.toObject(WaiterUserModel::class.java)
                        }
                    }
                }
            }.addOnFailureListener {
                Log.d("F1", it.message.toString())
            }
        }
    }

    fun cancelRsvp(documentId: String, eventName: String, user: UserModel) {
       gameWaitingListLiveData.apply {
           val query = getQuery(documentId, eventName)
           query.get().addOnSuccessListener {
               for (doc in it) {
                   doc.reference.collection("waiting_room").whereEqualTo("id", user.id).get()
                       .addOnSuccessListener { q ->
                           q.documents.forEach { waiter -> waiter.reference.delete() }
                       }
                   val toRemove =
                       if (doc.data["waiting"] == null) 1 else doc.data["waiting"].toString()
                           .toInt() - 1
                   doc.reference.update("waiting", toRemove)
                   doc.reference.collection("waiting_room").get().addOnSuccessListener {it->
                     value = it?.documents?.mapNotNull { d-> d.toObject(WaiterUserModel::class.java)}
                   }
               }
           }
       }
    }


    fun joinEvent(documentId:String, eventName:String, user:UserModel){
        playersLiveData.apply {
            val query = getQuery(documentId, eventName)
            query.get().addOnSuccessListener {
                for (event in it){
                    if (event.data["current_players"] != event.data["number_of_players"]){
                        val currentPlayers = if (event.data["current_players"] == null) 1 else event.data["current_players"].toString()
                            .toInt() + 1
                        event.reference.update("current_players", currentPlayers)

                        event.reference.collection("players").add(user).addOnFailureListener { ex->
                            Log.d("E", ex.message.toString())
                        }
                    }
                }
            }
        }
    }

    fun leaveEvent(documentId: String, eventName: String, user: UserModel){
        val query = getQuery(documentId, eventName)
        query.get().addOnSuccessListener {
            for (event in it){
                val currentPlayers = if (event.data["current_players"] == null) 1 else event.data["current_players"].toString()
                    .toInt() - 1
                event.reference.update("current_players", currentPlayers)
                event.reference.collection("players").whereEqualTo("id", user.id).get().addOnSuccessListener {
                    playerQuerySnapshot->
                    playerQuerySnapshot.documents.forEach {d-> d.reference.delete() }
                }
            }
        }
    }

    fun rsvp(documentId: String, eventName: String, user: UserModel) {
        val query = getQuery(documentId, eventName)
        query.get().addOnSuccessListener {
            for (doc in it) {
                val toAdd = if (doc.data["waiting"] == null) 1 else doc.data["waiting"].toString()
                    .toInt() + 1
                doc.reference.update("waiting", toAdd)
                doc.reference.collection("waiting_room").add(
                    hashMapOf(
                        "id" to user.id,
                        "name" to user.name
                    )
                ).addOnSuccessListener {
                    Log.d("S", "Success")
                }.addOnFailureListener { ex ->
                    Log.d("F", ex.message.toString())
                }
            }
        }.addOnFailureListener {
            Log.d("F", it.message.toString())
        }
    }

    fun confirmRsvp(
        documentId: String,
        eventName: String,
        userWaiterUserModel: WaiterUserModel
    ) {
        gameWaitingListLiveData.apply {
            val query = getQuery(documentId, eventName)
            query.get().addOnSuccessListener {
                for (doc in it) {
                    if (doc.data["current_players"].toString()
                            .toInt() < doc.data["number_of_players"].toString().toInt()
                    ) {
                        val currentPlayersCount = doc.data["current_players"].toString().toInt() + 1
                        val waitingRoomCount = doc.data["waiting"].toString().toInt()  - 1
                        doc.reference.collection("players").add(userWaiterUserModel)
                            .addOnSuccessListener {

                                doc.reference.collection("waiting_room")
                                    .whereEqualTo("id", userWaiterUserModel.id).get()
                                    .addOnSuccessListener {q->
                                        //delete the waiter from the collection
                                        q.documents.forEach { d-> d.reference.delete().addOnSuccessListener {
                                            Log.d("S", "Deleted")
                                            doc.reference.update("waiting", waitingRoomCount)
                                            doc.reference.update("current_players", currentPlayersCount)
                                            doc.reference.collection("waiting_room").get().addOnSuccessListener {snapshot->
                                                value = snapshot.documents.mapNotNull { doc-> doc.toObject(WaiterUserModel::class.java) }
                                            }.addOnFailureListener { ex->
                                                Log.d("F", ex.message.toString())
                                            }
                                        } }
                                    }

                            }.addOnFailureListener { ex->
                                Log.d("F", ex.message.toString())
                            }
                    }
                }
            }
        }
    }

    val eventDetails = eventDetailsLiveData
    val rsvpLiveData = rsvpAlreadySentLiveData
    val gamePlayersLiveData = playersLiveData
    val gameWaitingListLiveData = _gameWaitingListLiveData
    private fun getQuery(documentId: String, eventName: String): Query {
        return eventsRef.document(eventName.lowercase()).collection("items")
            .whereEqualTo(FieldPath.documentId(), documentId)
    }
}