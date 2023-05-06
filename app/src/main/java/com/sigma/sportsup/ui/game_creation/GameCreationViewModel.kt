package com.sigma.sportsup.ui.game_creation

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.data.UserModel
import com.sigma.sportsup.data.VenueEvent
import com.sigma.sportsup.data.VenueModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
                        value = !eventGameSnapshots.documents.any { eventSnapshot->
                            val venueEvent = eventSnapshot.toObject(VenueEvent::class.java)
                            Log.d("G", venueEvent.toString())

                            venueIsBusy(event, venueEvent!!)
                        }
                    }
                }
            }
        }
    }

    fun createEvent(context: Context, event: GameEvent, user: UserModel, onDone: ()->Any) {
        val db = Firebase.firestore
        db.collection("games")
            .document(event.name.toString())
            .collection("items")
            .add(event)
            .addOnSuccessListener { doc ->

                //add a game snapshot to the venues
                db.collection("venues").whereEqualTo("name", event.venue).get()
                    .addOnSuccessListener {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun venueIsBusy(createGame: GameEvent, gameDB:VenueEvent):Boolean{

        return createGame.date == gameDB.date && getTimeLong(gameDB.start_time!!) >= getTimeLong(createGame.start_time!!) && getTimeLong(createGame.start_time!!) <= getTimeLong(gameDB.end_time!!)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimeLong(time:String):Long {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(formatStringTime(time), formatter).toNanoOfDay()/1_000_000
    }
    private fun formatStringTime(time: String):String {
        return time.split(":").joinToString(":") { it.padStart(2, '0') }
    }

    val games: MutableLiveData<List<GameModel>?> = _games
    val venues: MutableLiveData<List<VenueModel>?> = _venues
    val venueAvailability:MutableLiveData<Boolean> = venueAvailabilityMutableLiveData
}