package com.sigma.sportsup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.databinding.FragmentEventPlayersBinding
import com.sigma.sportsup.ui.events.MyEventsViewModel
import com.sigma.sportsup.ui.search.SearchViewModel

class EventPlayersFragment:Fragment() {

    private var _binding: FragmentEventPlayersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var eventsViewModel: EventDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEventPlayersBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsViewModel = ViewModelProvider(this).get(EventDetailsViewModel::class.java)

        var eventId: String? = null
        var eventName: String? = null
        if (arguments != null) {
            eventId = arguments?.getString("eventId")
            eventName = arguments?.getString("eventName")
        }

        eventId?.let { eventsViewModel.getGamePlayers(it, eventName!!) }

        eventsViewModel.gamePlayersLiveData.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                val adapter = EventPlayersItemAdapter(requireContext(), it)
                binding.recyclerViewPlayers.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}