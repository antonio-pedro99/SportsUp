package com.sigma.sportsup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.R
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.databinding.FragmentEventBinding

class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null

    private val binding get() = _binding!!

    private lateinit var eventsViewModel: EventDetailsViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsViewModel = ViewModelProvider(this).get(EventDetailsViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        setHasOptionsMenu(true)

        var eventId: String? = null
        var eventName: String? = null
        if (arguments != null) {
            eventId = arguments?.getString("eventId")
            eventName = arguments?.getString("eventName")
        }

        eventId?.let { eventsViewModel.getEventById(it, eventName!!) }

        eventsViewModel.eventDetails.observe(this) {
            val resources = activity?.resources

            binding.txtEventName.text =
                resources?.getString(R.string.txt_event_details_name, it.name)
            binding.txtEventDetailsTime.text =
                resources?.getString(R.string.txt_event_details_time_value, it.start_time, it.end_time)
            binding.txtEventDetailsDate.text =
                resources?.getString(R.string.txt_event_details_date_value, it.date)
            binding.txtEventDetailsVenue.text =
                resources?.getString(R.string.txt_event_details_venue, it.venue)

            val badgePlayers = binding.tabLayout.getTabAt(0)?.orCreateBadge
            badgePlayers?.number = it.current_players!!

            userViewModel.currentUser.observe(this) { user ->
                if (it.host_ref == user.id) {
                    Log.d("Fab", user.id!!)
                    binding.fabEdt.visibility - View.VISIBLE
                } else {
                    binding.tabLayout.removeTabAt(2)
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.event_details_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                true
            }
            R.id.menu_join -> {
                true
            }
            R.id.menu_save -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}