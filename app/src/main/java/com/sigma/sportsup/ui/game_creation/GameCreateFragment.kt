package com.sigma.sportsup.ui.game_creation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.data.GameEvent
import com.sigma.sportsup.data.UserModel
import com.sigma.sportsup.databinding.FragmentGameCreateFragmentBinding
import java.text.SimpleDateFormat
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnInvite = binding.btnCreateEvent

        val rgAudience = binding.audienceGroup
        val btnCancel = binding.btnCancelCreating
        val edtVenue = binding.editVenue
        val edtGame = binding.edtGame
        val edtNumberOfPlayers = binding.edtNumberPlayers
        val edtDate = binding.edtDate
        val edtTime = binding.editTime
        val edtEndTime = binding.edtEndTime
        // val edtDurationFormat = binding.editDurationFormat
        //val edtDuration = binding.edtDuration

        selectDate()
        selectTime(binding.editTime)
        selectTime(binding.edtEndTime)
        var audience = "Everyone"

        rgAudience.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbEveryone.id -> audience = binding.rbEveryone.text.toString()
                binding.rbMyFriends.id -> audience = binding.rbMyFriends.text.toString()
            }
        }

        //  buildUnitOfTime()
        showAvailableVenues()
        btnInvite.setOnClickListener {


            val eventsViewModel = ViewModelProvider(this).get(GameCreationViewModel::class.java)

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
            eventsViewModel.createEvent(requireContext(), user = user, event = event, onDone = {
                clearForm()
                showEventCreatedDialog()
            })
        }

        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun showAvailableVenues() {
        val db = Firebase.firestore

        val date = Date(System.currentTimeMillis())
        db.collectionGroup("venues").whereEqualTo(
            "date", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(date)
        ).get().addOnSuccessListener { query ->
            if (!query.isEmpty) {
                val doc = query.documents[0]
                val path = doc.reference.path

                Log.d("LOG", path)

            } else {
                Log.d("LOG", "Venue is free")
            }
        }
    }

    private fun showEventCreatedDialog() {
        MaterialAlertDialogBuilder(mContext)
            .setTitle("Information")
            .setMessage("Your Event has been created! Do you want to see it now?")
            .setNegativeButton("No thanks") { _, _ -> findNavController().navigateUp() }
            .setPositiveButton("Yes Please") { _, _ -> }
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

    private fun selectDate(): String {
        val edtDate = binding.edtDate
        edtDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date")
                .setSelection(System.currentTimeMillis())
                .setSelection(
                    MaterialDatePicker
                        .todayInUtcMilliseconds()
                )
                .build()


            datePicker.addOnPositiveButtonClickListener {
                val selectedDate = Date(it)
                val formattedDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
                edtDate.setText(formattedDate)
            }

            datePicker.showNow(parentFragmentManager, "date")
        }

        return edtDate.text.toString()
    }

    private fun selectTime(editTime: EditText): String {

        editTime.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(System.currentTimeMillis().toInt())
                .setTitleText("Select Event Time")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val selectedTime = "${timePicker.hour}:${timePicker.minute}"
                editTime.setText(selectedTime)
            }

            timePicker.show(parentFragmentManager, "time")
        }

        return editTime.text.toString()
    }

    private fun buildVenuesItem(data: List<String>) {
        val selectedGame = binding.editVenue
        val adapter =
            ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, data);
        selectedGame.setAdapter(adapter)
    }

    /* private fun buildUnitOfTime() {
         val unitOfTimeEditText = binding.editDurationFormat
         val adapter = ArrayAdapter<String>(
             mContext, android.R.layout.simple_dropdown_item_1line, listOf<String>(
                 "Hour", "Minute"
             )
         )
         unitOfTimeEditText.setAdapter(adapter)
     }*/

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