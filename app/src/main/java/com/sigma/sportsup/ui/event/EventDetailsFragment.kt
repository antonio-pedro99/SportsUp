package com.sigma.sportsup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.R
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.databinding.FragmentEventBinding

class EventDetailsFragment: Fragment() {

    private var _binding: FragmentEventBinding? = null

    private val binding get() = _binding!!

   // private lateinit var eventsViewModel:EventDetailsViewModel
    //private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        val eventsViewModel = ViewModelProvider(this).get(EventDetailsViewModel::class.java)
        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        var eventId: String? = null
        var eventName:String? = null
        if (arguments != null) {
            eventId = arguments?.getString("eventId")
            eventName = arguments?.getString("eventName")
        }

        eventId?.let { eventsViewModel.getEventById(it, eventName!!) }

        eventsViewModel.eventDetails.observe(this) {
            val resources = activity?.resources

            binding.txtEventName .text = resources?.getString(R.string.txt_event_details_name, it.name)
            binding.txtEventDetailsTime.text = resources?.getString(R.string.txt_event_details_time_value, it.time, it.end_time)
            binding.txtEventDetailsDate.text = resources?.getString(R.string.txt_event_details_date_value, it.date)
            binding.txtEventDetailsVenue.text = resources?.getString(R.string.txt_event_details_venue, it.venue)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}