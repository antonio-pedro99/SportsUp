package com.sigma.sportsup.ui.game_creation

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sigma.sportsup.databinding.FragmentGameCreateFragmentBinding
import com.sigma.sportsup.ui.events.EventsViewModel

class GameCreateFragment:Fragment() {


    private var _binding : FragmentGameCreateFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val gameCreateViewModel = ViewModelProvider(this).get(GameCreationViewModel::class.java)

        _binding = FragmentGameCreateFragmentBinding.inflate(inflater, container, false)

        val root: View = _binding!!.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var btnInvite = binding.btnCreateEvent

        btnInvite.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}