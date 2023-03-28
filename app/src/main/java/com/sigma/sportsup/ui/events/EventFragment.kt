package com.sigma.sportsup.ui.events

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.R
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.databinding.FragmentEventsBinding
import com.sigma.sportsup.ui.game_creation.GameCreateFragment

class EventFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val events = listOf<GameEvent>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)

        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        eventsViewModel.games.observe(viewLifecycleOwner) { it ->
            val gamesNames = it?.map { it.name }
            buildGamesItem(gamesNames!!)
        }


        eventsViewModel.events.observe(viewLifecycleOwner) {it->
            buildEventsList(it)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btnCreateNewEvent = binding.btnCreateEvent

        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        btnCreateNewEvent.setOnClickListener {
            findNavController(this).navigate(R.id.action_navigation_events_to_navigation_game_create)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildGamesItem(data: List<String>){
        val selectedGame = binding.edtiTextGame
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, data)
        selectedGame.setAdapter(adapter)
    }

    private fun buildEventsList(data: List<GameEvent>){
        val recyclerView = binding.eventsRecyclerView
        recyclerView.setHasFixedSize(true)
        val adapterItem = GameEventItemAdapter(requireContext(), data)
        recyclerView.adapter = adapterItem
    }
}