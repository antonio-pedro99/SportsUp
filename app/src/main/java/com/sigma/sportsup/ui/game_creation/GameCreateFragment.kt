package com.sigma.sportsup.ui.game_creation

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        // val edtDurationFormat = binding.editDurationFormat
        //val edtDuration = binding.edtDuration

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
                            showEventCreatedDialog()
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


    private fun showEventCreatedDialog() {
        MaterialAlertDialogBuilder(mContext)
            .setTitle("Information")
            .setMessage("Your Event has been created! Do you want to see it now?")
            .setNegativeButton("No thanks") { _, _ -> findNavController().navigateUp() }
            .setPositiveButton("Yes Please") { _, _ -> findNavController() }
            .show()
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


}