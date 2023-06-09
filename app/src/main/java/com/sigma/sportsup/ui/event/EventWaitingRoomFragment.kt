package com.sigma.sportsup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.databinding.FragmentEventWaitingRoomBinding

class EventWaitingRoomFragment:Fragment() {

    private var _binding: FragmentEventWaitingRoomBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var eventsViewModel: EventDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEventWaitingRoomBinding.inflate(inflater, container, false)
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

        eventId?.let { eventsViewModel.getWaitingList(it, eventName!!) }

        eventsViewModel.gameWaitingListLiveData.observe(viewLifecycleOwner){ waiters->
            if(waiters.isNotEmpty()){
                val adapter = EventWaitingRoomItemAdapter(requireContext(), waiters) {pos->
                    eventsViewModel.confirmRsvp(eventId!!, eventName!!, waiters[pos])
                }
                Log.d("S", waiters.first().name.toString())
                binding.recyclerViewWaitingRoom.adapter = adapter
            } else {
                Log.d("F", "Failed")
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}