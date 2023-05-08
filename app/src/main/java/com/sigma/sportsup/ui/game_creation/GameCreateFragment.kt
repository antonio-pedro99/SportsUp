package com.sigma.sportsup.ui.game_creation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.sigma.sportsup.MainActivity
import com.sigma.sportsup.R
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.UserModel
import com.sigma.sportsup.databinding.FragmentGameCreateFragmentBinding
import com.sigma.sportsup.utils.SportsUpEventUtils
import com.sigma.sportsup.utils.SportsUpTimeDateUtils
import java.util.*

class GameCreateFragment : Fragment() {


    private var _binding: FragmentGameCreateFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context

    private lateinit var user: UserModel

    private lateinit var handler: ActivityResultLauncher<Array<String>>
    private lateinit var addEventToCalendarHandler: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val gameCreateViewModel = ViewModelProvider(this).get(GameCreationViewModel::class.java)
        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        _binding = FragmentGameCreateFragmentBinding.inflate(inflater, container, false)

        gameCreateViewModel.venues.observe(viewLifecycleOwner) { it ->
            val venues = it?.map { it.name }
            buildVenuesItem(venues as List<String>)
        }

        gameCreateViewModel.games.observe(viewLifecycleOwner) { it ->
            val games = it?.map { it.name }
            buildGamesItems(games!!)
        }

        handler = requestMultiplePermissionLauncher {
            Log.d("TAG", "onCreateView: ${it.toString()}")
        }

        addEventToCalendarHandler = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            Log.d("G","onCreateView: ${it.data.toString()} Code: ${it.resultCode}")
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                Toast.makeText(mContext, "Event added to calendar", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigate(R.id.action_navigation_game_create_to_navigation_event_my_event)
           // parentFragmentManager.popBackStack()
        }

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        userViewModel.currentUser.observe(viewLifecycleOwner) { user = it }
        val root: View = _binding!!.root
        return root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameCreationViewModel = ViewModelProvider(this).get(GameCreationViewModel::class.java)
        val btnInvite = binding.btnCreateEvent

        val rgAudience = binding.audienceGroup
        val btnCancel = binding.btnCancelCreating
        val edtVenue = binding.editVenue
        val edtGame = binding.edtGame
        val edtNumberOfPlayers = binding.edtNumberPlayers
        val edtDate = binding.edtDate
        val edtTime = binding.editTime
        val edtEndTime = binding.edtEndTime
        val edtGameEventName = binding.edtEventGameName

        SportsUpTimeDateUtils.showDatePickerDialog(requireContext(), parentFragmentManager, edtDate)
        SportsUpTimeDateUtils.showTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.editTime,
            "Start Time"
        )
        SportsUpTimeDateUtils.showTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.edtEndTime,
            "End Time"
        )
        var audience = "Everyone"

        rgAudience.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbEveryone.id -> audience = binding.rbEveryone.text.toString()
                binding.rbMyFriends.id -> audience = binding.rbMyFriends.text.toString()
            }
        }

        btnInvite.setOnClickListener {

            if (edtDate.text.isNullOrEmpty() && edtGameEventName.text.isNullOrEmpty() &&
                edtDate.text.isNullOrEmpty() && edtTime.text.isNullOrEmpty()
                && edtEndTime.text.isNullOrEmpty() && edtNumberOfPlayers.text.isNullOrEmpty()
                && edtVenue.text.isNullOrEmpty() && edtGame.text.isNullOrEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    "One or more fields are required.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val eventName = if (!edtGame.text.toString().contains("")) edtGame.text.toString()
                else edtGame.text.replace(Regex("\\s"), "_").lowercase()
                val event = GameEvent()
                event.name = eventName
                event.host = user.name
                event.host_ref = user.id
                event.venue = edtVenue.text.toString()
                event.date = edtDate.text.toString()
                event.start_time = edtTime.text.toString()
                event.end_time = edtEndTime.text.toString()
                event.number_of_players = edtNumberOfPlayers.text.toString().toInt()
                event.audience = audience
                event.current_players = 1
                event.game_event_name = edtGameEventName.text.toString()

                val eventStatus = SportsUpEventUtils.isEventValid(event)
                gameCreationViewModel.checkVenueAvailability(event)
                val venueIsBusy = gameCreationViewModel.venueAvailability.value


                Log.d("G", "onViewCreated: $venueIsBusy")
                if (venueIsBusy != true && eventStatus == "valid"
                ) {
                   /* Toast.makeText(
                        requireContext(),
                        "Event created successfully",
                        Toast.LENGTH_LONG
                    ).show()*/
                  gameCreationViewModel.createEvent(
                        requireContext(),
                        user = user,
                        event = event,
                        onDone = {
                            clearForm()
                            showEventCreatedDialog(gameCreationViewModel, event)
                        })
                } else if (venueIsBusy == true) {
                    Toast.makeText(
                        requireContext(),
                        "This venue is not available at this time",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        eventStatus,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun showEventCreatedDialog(gameCreationViewModel: GameCreationViewModel, event: GameEvent) {

        MaterialAlertDialogBuilder(mContext)
            .setTitle("Information")
            .setMessage("Your Event has been created! Do you want to add to your Google Calendar?")
            .setNegativeButton("No thanks") { _, _ -> findNavController().navigateUp() }
            .setPositiveButton("Yes Please") { _, _ ->
                    addToCalendar(event, user)
             }
            .show()
    }

    private fun addToCalendar(event: GameEvent, userModel: UserModel){

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            handler.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                ))
        } else {
            val eventBeginTime = SportsUpTimeDateUtils.getDateTime(event.date!!, event.start_time!!)
            val eventEndTime = SportsUpTimeDateUtils.getDateTime(event.date!!, event.end_time!!)

            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, event.game_event_name)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.venue)
                .putExtra(CalendarContract.Events.DESCRIPTION, "A ${event.name} match with your buddies")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventBeginTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndTime)
                .putExtra(CalendarContract.Events.OWNER_ACCOUNT, userModel.phone)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            //startActivityForResult(intent, 0)
            addEventToCalendarHandler.launch(intent)
        }

    }

    private fun clearForm() {
        binding.edtDate.clearComposingText()
        binding.editTime.clearComposingText()
        binding.editVenue.clearListSelection()
        binding.edtGame.clearListSelection()
        binding.edtNumberPlayers.clearComposingText()
        binding.audienceGroup.clearCheck()
    }


    private fun buildVenuesItem(data: List<String>) {
        val selectedGame = binding.editVenue
        val adapter =
            ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, data);
        selectedGame.setAdapter(adapter)
    }


    private fun buildGamesItems(data: List<String>) {
        val selectedGame = binding.edtGame
        val adapter =
            ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, data);
        selectedGame.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    private  val requestMultiplePermissionLauncher = {onResult:(Map<String, Boolean>)->Unit->
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onResult(result)
        }
    }

    private val requestSinglePermissionLauncher = { onGranted:()->Unit , onDenied: () -> Unit->
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

}