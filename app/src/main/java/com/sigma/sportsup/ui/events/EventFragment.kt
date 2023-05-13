package com.sigma.sportsup.ui.events

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.sigma.sportsup.R
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.databinding.FragmentEventsBinding
import com.sigma.sportsup.ui.game_creation.GameCreateFragment

class EventFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null

    private var gameEvents : List<GameModel> = ArrayList<GameModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var gotArguments = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)
        setHasOptionsMenu(arguments == null)

        val gameNames = mutableListOf<String>("All")
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        eventsViewModel.games.observe(viewLifecycleOwner) { it ->
            gameNames.addAll(1, it?.map { it.name } as MutableList<String>)
            buildGamesItem(gameNames)
        }


        if (arguments != null){
            val gameName = arguments?.getString("gameName")!!
            gotArguments = true
            binding.edtiTextGame.setText(gameName)

        }

        eventsViewModel.events.observe(viewLifecycleOwner) {it->

            if (arguments != null){
                val gameName = arguments?.getString("gameName")!!
                buildEventsList(it.filter { event-> event.name!!.lowercase() == gameName.lowercase() })
            } else {
                arguments = null
                buildEventsList(it)
            }
            binding.edtiTextGame.setOnItemClickListener { _, _, position, _ ->
                if (position == 0){
                    buildEventsList(it)
                } else {
                    if (arguments != null){
                        val gameName = arguments?.getString("gameName")!!
                        buildEventsList(it.filter { event-> event.name!!.lowercase() == gameName.lowercase() })
                    } else {
                        arguments = null
                        buildEventsList(it.filter { event-> event.name!!.lowercase() == gameNames[position].lowercase() })
                    }
                }
                arguments = null
            }

        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnCreateNewEvent = binding.btnCreateEvent

        if (arguments != null){
            binding.btnCreateEvent.visibility = View.GONE
        }
        btnCreateNewEvent.setOnClickListener {
            if (gotArguments){
                Log.d("EVENTS", "got arguments")

                it.findNavController().navigate(R.id.action_eventFragment_to_gameCreateFragment)
            } else {
                Log.d("EVENTS", "no arguments")
                findNavController(this).navigate(R.id.action_navigation_events_to_navigation_game_create)
            }
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.events_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.menu_my_events -> {
                 findNavController().navigate(R.id.action_nav_events_to_navigation_event_my_event)
                true
            }

            else-> super.onOptionsItemSelected(item)
        }
    }
}