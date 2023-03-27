package com.sigma.sportsup.ui.game_creation

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.ui.events.EventsViewModel

class GameCreateFragment:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)

        return inflater.inflate(com.sigma.sportsup.R.layout.fragment_game_create_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

}