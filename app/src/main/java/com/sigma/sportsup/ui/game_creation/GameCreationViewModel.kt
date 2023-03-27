package com.sigma.sportsup.ui.game_creation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.data.VenueModel

class GameCreationViewModel: ViewModel() {

    private val gameRef = FirestoreCollection().games
    private val venuesRef = FirestoreCollection().venues

    private val _games = MutableLiveData<List<GameModel>?>().apply {

        gameRef.addSnapshotListener { snapshot, exception ->
            if (exception!=null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot -> documentSnapshot.toObject(
                GameModel::class.java) }
            value = items
        }
    }


    private val _venues = MutableLiveData<List<VenueModel>?>().apply {
        venuesRef.addSnapshotListener { snapshot, exception ->
            if (exception !=null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot -> documentSnapshot.toObject(VenueModel::class.java) }
            value = items
        }
    }

    val games : MutableLiveData<List<GameModel>?> = _games
    val venues : MutableLiveData<List<VenueModel>?> = _venues
}