package com.sigma.sportsup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.sigma.sportsup.EventDetailsActivity
import com.sigma.sportsup.MainActivity
import com.sigma.sportsup.R
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.databinding.FragmentEventBinding
import com.sigma.sportsup.system.NotificationUtils

class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null

    private val binding get() = _binding!!

    private lateinit var eventsViewModel: EventDetailsViewModel
    private lateinit var userViewModel: UserViewModel

    private val pages =  listOf(
        EventPlayersFragment(),
        EventCommentsFragment(),
    )

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
        eventId?.let { eventsViewModel.getGamePlayers(it, eventName!!) }

        eventsViewModel.eventDetails.observe(viewLifecycleOwner) {eventGame ->
            val resources = activity?.resources

            binding.txtEventName.text =
                resources?.getString(R.string.txt_event_details_name, eventGame.game_event_name ?: eventGame.name)
            binding.txtEventDetailsTime.text =
                resources?.getString(
                    R.string.txt_event_details_time_value,
                    eventGame.start_time,
                    eventGame.end_time
                )
            binding.txtEventDetailsDate.text =
                resources?.getString(R.string.txt_event_details_date_value, eventGame.date)
            binding.txtEventDetailsVenue.text =
                resources?.getString(R.string.txt_event_details_venue, eventGame.venue)

            //add arguments to each Page
            pages.forEach{page ->
                val args = Bundle()
                args.putString("eventId", eventGame.id)
                args.putString("eventName", eventGame.name)
                page.arguments = args
            }

            val adapter = EventDetailsPagerAdapter(requireActivity(), pages.subList(0, 2))

            binding.viewPagerContent.adapter = adapter

            TabLayoutMediator(binding.tabLayout, binding.viewPagerContent){tab, position ->
                when(position){
                    0-> {
                        tab.text = activity?.resources?.getString(R.string.tab_item_players)
                        tab.orCreateBadge.number = eventGame.current_players!!
                    }
                    1-> tab.text = activity?.resources?.getString(R.string.tab_item_comments)
                }
            }.attach()

            userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
                eventId?.let {evId-> eventsViewModel.checkRsvp(evId, eventName!!, user) }

                if (eventGame.host_ref == user.id) {
                    Log.d("Fab", user.id!!)
                    binding.fabEdt.visibility = View.VISIBLE
                    binding.fabAction.setImageResource(R.drawable.baseline_done_24)
                    binding.fabEdt.setOnClickListener() {

                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to delete the Game?")
                            .setNegativeButton("Yes") { _, _ ->
                                eventsViewModel.deleteEvent(eventId!!, eventName!!)
                                //delete the event from the calendar



                                (activity as EventDetailsActivity).finish()
                            }
                            .setPositiveButton("No") { _, _ -> }
                            .show()
                    }
                } else {
                    eventsViewModel.gamePlayersLiveData.observe(viewLifecycleOwner) { players ->
                        if (players.any { playerUser -> playerUser.id == user.id }) {
                            binding.fabAction.setImageResource(R.drawable.baseline_free_cancellation_24)
                            binding.fabAction.setOnClickListener {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Confirmation")
                                    .setMessage("You already part of the Game! Would you like to leave?")
                                    .setNegativeButton("Yes") { _, _ ->
                                       eventsViewModel.leaveEvent(eventId!!, eventName!!, user)
                                    }
                                    .setPositiveButton("No") { _, _ -> }
                                    .show()
                            }

                        } else {
                            binding.fabAction.setImageResource(R.drawable.baseline_event_24)
                            binding.fabAction.setOnClickListener {
                                eventsViewModel.joinEvent(eventId!!, eventName!!, user)
                                FirebaseMessaging.getInstance().subscribeToTopic(eventId)
                                NotificationUtils().sendNotificationToTopic(
                                   eventId,
                                    "New Player Joined the Game",
                                    "A new player has joined the game ${eventGame.game_event_name?: eventGame.name}")
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Congratulations")
                                    .setMessage("You are now part of the game")
                                    .setPositiveButton("Sounds Good!") { _, _ -> }
                                    .show()
                            }
                        }
                    }
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