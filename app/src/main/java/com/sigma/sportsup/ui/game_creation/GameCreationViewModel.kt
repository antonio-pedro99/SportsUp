package com.sigma.sportsup.ui.game_creation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.MainActivity
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.data.UserModel
import com.sigma.sportsup.data.VenueEvent
import com.sigma.sportsup.data.VenueModel
import com.sigma.sportsup.utils.SportsUpEventUtils
import com.sigma.sportsup.utils.SportsUpTimeDateUtils

class GameCreationViewModel : ViewModel() {

    private val gameRef = FirestoreCollection().games
    private val venuesRef = FirestoreCollection().venues

    private val _games = MutableLiveData<List<GameModel>?>().apply {

        gameRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(
                    GameModel::class.java
                )
            }
            value = items
        }
    }

    private val venueAvailabilityMutableLiveData = MutableLiveData<Boolean>()


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkVenueAvailability(event: GameEvent){
        val db = Firebase.firestore
        venueAvailabilityMutableLiveData.apply {
            db.collection("venues").whereEqualTo("name", event.venue).get().addOnSuccessListener {
                for (document in it){
                    document.reference.collection("games").get().addOnSuccessListener {eventGameSnapshots->
                        value = eventGameSnapshots.documents.any { eventSnapshot->
                            val venueEvent = eventSnapshot.toObject(VenueEvent::class.java)

                            venueIsBusy(event, venueEvent!!)
                        }
                    }
                }
            }
        }
    }



    fun createEvent(context: Context, event: GameEvent, user: UserModel, onDone: ()->Any):String? {
        val db = Firebase.firestore
        var eventId: String? = null
        db.collection("games")
            .document(event.name.toString())
            .collection("items")
            .add(event)
            .addOnSuccessListener { doc ->
                //add a game snapshot to the venues
                FirebaseMessaging.getInstance().subscribeToTopic(doc.id)
                db.collection("venues").whereEqualTo("name", event.venue).get()
                    .addOnSuccessListener {
                        eventId = doc.id
                        // Toast.makeText(requireContext(), it.documents.toString(), Toast.LENGTH_LONG).show()
                        for (document in it) {
                            val gameSessionVenue = hashMapOf(
                                "session" to doc.id,
                                "date" to event.date,
                                "start_time" to event.start_time,
                                "end_time" to event.end_time,
                                "status" to "Planned"
                                // "duration" to event["duration"]
                            )
                            val ref = document.reference.collection("games").document()
                            ref.set(gameSessionVenue).addOnFailureListener { exception ->
                                Toast.makeText(
                                    context,
                                    exception.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                                .addOnSuccessListener {
                                    print("Done")
                                }
                        }
                    }

                //add players to game, the first player will always be the host
                doc.collection("players").add(
                    hashMapOf(
                        "name" to user.name,
                        "id" to user.id
                    )
                )
                    .addOnFailureListener {
                        Toast.makeText(context, it.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                onDone()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed with ${exception.message}", Toast.LENGTH_LONG)
                    .show()
            }

        return eventId
    }

    private val _venues = MutableLiveData<List<VenueModel>?>().apply {
        venuesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(VenueModel::class.java)
            }
            value = items
        }
    }

    private fun venueIsBusy(createGame: GameEvent, gameDB: VenueEvent): Boolean {

        if (createGame.date != gameDB.date) return false
      /*  if (createGame.start_time!! >= gameDB.start_time!! && createGame.start_time!! <= gameDB.end_time!!) return true
        if (createGame.end_time!! >= gameDB.start_time!! && createGame.end_time!! <= gameDB.end_time!!) return true
        if (createGame.start_time!! <= gameDB.start_time!! && createGame.end_time!! >= gameDB.end_time!!) return true
        if (createGame.start_time!! >= gameDB.start_time!! && createGame.end_time!! <= gameDB.end_time!!) return true*/
        return SportsUpEventUtils.isVenueBusy(createGame, gameDB)

        //return SportsUpEventUtils.isVenueBusy(createGame, gameDB) && createGame.date == gameDB.date
    }

    val games: MutableLiveData<List<GameModel>?> = _games
    val venues: MutableLiveData<List<VenueModel>?> = _venues
    val venueAvailability:MutableLiveData<Boolean> = venueAvailabilityMutableLiveData
}