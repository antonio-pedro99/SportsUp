package com.sigma.sportsup.ui.home

import android.se.omapi.Session
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.sigma.sportsup.FirestoreCollection
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.data.SessionEvent

class HomeViewModel : ViewModel() {

    private val gameRef = FirestoreCollection().games

    private val _games = MutableLiveData<List<GameModel>?>().apply {
        gameRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val items = snapshot?.documents?.mapNotNull { documentSnapshot -> documentSnapshot.toObject(GameModel::class.java) }
            value = items
        }
    }

    private val _sessions = MutableLiveData<List<SessionEvent>>().apply {
        value = listOf(
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM"),
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM"),
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM"),
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM")
        )
    }

    val games: LiveData<List<GameModel>?> = _games
    val sessions:LiveData<List<SessionEvent>> = _sessions


}