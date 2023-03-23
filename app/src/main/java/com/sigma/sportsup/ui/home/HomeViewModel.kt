package com.sigma.sportsup.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sigma.sportsup.data.GameModel

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
    val text: LiveData<String> = _text
    val games: LiveData<List<GameModel>> = _games


}