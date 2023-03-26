package com.sigma.sportsup.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.data.GameModel
import com.sigma.sportsup.databinding.FragmentEventsBinding

class EventFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)

        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        eventsViewModel.games.observe(viewLifecycleOwner) { it ->
            val gamesNames = it?.map { it.name }
            buildGamesItem(gamesNames!!)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildGamesItem(data: List<String>){
        val selectedGame = binding.edtiTextGame
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, data);
        selectedGame.setAdapter(adapter)
    }
}