package com.sigma.sportsup.ui.home

import android.se.omapi.Session
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.data.SessionEvent

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val _games = MutableLiveData<List<GameModel>>().apply {
        value = listOf(
            GameModel(name = "Cricket"),
            GameModel(name = "Volleyball"),
            GameModel(name = "Tennis Table"),
            GameModel(name = "Basket Ball"),
            GameModel(name = "Basket Ball"),
            GameModel(name = "Basket Ball"),
            GameModel(name = "Basket Ball"),
            GameModel(name = "Basket Ball"),
            GameModel(name = "Basket Ball"),
            GameModel(name = "Cricket"),
            GameModel(name = "Volleyball")
        )
    }

    private val _sessions = MutableLiveData<List<SessionEvent>>().apply {
        value = listOf(
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM"),
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM"),
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM"),
            SessionEvent(name = "Sigmas X", "Antonio Pedro", "Footbal Ground", "4:30 PM")
        )
    }
    val text: LiveData<String> = _text
    val games: LiveData<List<GameModel>> = _games
    val sessions:LiveData<List<SessionEvent>> = _sessions


}