package com.sigma.sportsup.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.*
import android.widget.Button
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.protobuf.Empty
import com.sigma.sportsup.MainActivity
import com.sigma.sportsup.R
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.UserModel
import com.sigma.sportsup.databinding.FragmentHomeBinding
import com.sigma.sportsup.ui.chat.ChatBox
import com.sigma.sportsup.ui.chat.ChatConversationsActivity
import com.sigma.sportsup.ui.event.EventDetailsViewModel
import com.sigma.sportsup.ui.events.EventsViewModel
import com.sigma.sportsup.utils.SportsUpTimeDateUtils


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var handler: ActivityResultLauncher<String>
    private lateinit var mHandler: ActivityResultLauncher<Array<String>>
    private lateinit var multiplePermissionHandler: ActivityResultLauncher<Array<String>>
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var addEventToCalendarHandler: ActivityResultLauncher<Intent>

    private lateinit var eventsViewModel: EventDetailsViewModel
    private lateinit var userViewModel: UserViewModel


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val eventsViewModel = ViewModelProvider(this).get(EventDetailsViewModel::class.java)
        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gameRecycler = binding.gamesRecyclerview
        val sessionRecycler = binding.sessionsRecyclerview



        handler = requestSinglePermissionLauncher(
            {
                val myIntent = Intent(this@HomeFragment.context, ChatBox::class.java)
                this@HomeFragment.startActivity(myIntent)
            },
            {
                Toast.makeText(
                    requireContext(),
                    "Permission denied: Please all the app not send notifications",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        mHandler = requestMultiplePermissionLauncher({

        })

        multiplePermissionHandler = requestMultiplePermissionLauncher {

        }

        addEventToCalendarHandler = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }


        sessionRecycler.setHasFixedSize(true)
        gameRecycler.setHasFixedSize(true)

        homeViewModel.games.observe(viewLifecycleOwner) {
            gameRecycler.adapter = GamesItemAdapter(context = requireContext(), it!!)
            gameRecycler.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        }


        userViewModel.currentUser.observe(viewLifecycleOwner) {user->
            homeViewModel.sessions.observe(viewLifecycleOwner) {

                if (it.isNotEmpty()) {
                    sessionRecycler.adapter = SessionsItemAdapter(
                        requireContext(), user, it,

                        //on join clicked
                        { game ->

                           if(user.id == game.host_ref){
                                 Toast.makeText(requireContext(), "You are the host of this game", Toast.LENGTH_SHORT).show()
                           } else{
                               eventsViewModel.joinEvent(game.id!!, game.name!!, user)
                               MaterialAlertDialogBuilder(requireContext())
                                   .setTitle("Confirmation")
                                   .setMessage("You have joined the game, want to add to calendar?")
                                   .setNegativeButton("Yes") { _, _ ->
                                       addToCalendar(
                                           game,
                                           user
                                       )
                                   }
                                   .setPositiveButton("No") { _, _ -> }
                                   .show()
                           }

                        },
                        //on item clicked
                        {
                            //show something
                            Toast.makeText(requireContext(), "Clicked on item $it", Toast.LENGTH_SHORT)
                                .show()
                        }, //on save clicked
                    )
                    sessionRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.txtNoOngoingEvent.visibility = View.INVISIBLE
                } else {
                    binding.txtNoOngoingEvent.visibility = View.VISIBLE
                }
            }
        }


        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.chat_menu -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.GET_ACCOUNTS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val myIntent = Intent(this@HomeFragment.context, ChatBox::class.java)
                    this@HomeFragment.startActivity(myIntent)
                } else {
                    multiplePermissionHandler.launch(
                        arrayOf(
                            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
                            Manifest.permission.GET_ACCOUNTS

                        )
                    )
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun addToCalendar(event: GameEvent, userModel: UserModel) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            mHandler.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            ).also {
                val eventBeginTime = SportsUpTimeDateUtils.getDateTime(event.date!!, event.start_time!!)
                val eventEndTime = SportsUpTimeDateUtils.getDateTime(event.date!!, event.end_time!!)

                val intent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, event.game_event_name)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, event.venue)
                    .putExtra(
                        CalendarContract.Events.DESCRIPTION,
                        "A ${event.name} match with your buddies"
                    )
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventBeginTime)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndTime)
                    .putExtra(CalendarContract.Events.OWNER_ACCOUNT, userModel.phone)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                //startActivityForResult(intent, 0)
                addEventToCalendarHandler.launch(intent)
            }
        } else {
            val eventBeginTime = SportsUpTimeDateUtils.getDateTime(event.date!!, event.start_time!!)
            val eventEndTime = SportsUpTimeDateUtils.getDateTime(event.date!!, event.end_time!!)

            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, event.game_event_name)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.venue)
                .putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    "A ${event.name} match with your buddies"
                )
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventBeginTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndTime)
                .putExtra(CalendarContract.Events.OWNER_ACCOUNT, userModel.phone)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            //startActivityForResult(intent, 0)
            addEventToCalendarHandler.launch(intent)
        }

    }

    private val requestSinglePermissionLauncher = { onGranted: () -> Unit, onDenied: () -> Unit ->
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("Home", ": granted")
                onGranted()
            } else {
                Log.d("Home", ": denied")
                onDenied()
            }
        }
    }

    private val requestMultiplePermissionLauncher = { onResult: (Map<String, Boolean>) -> Unit ->
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onResult(result)
        }
    }


}