package com.sigma.sportsup.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.databinding.FragmentEventCommentsBinding
import com.sigma.sportsup.ui.search.SearchViewModel

class EventCommentsFragment:Fragment() {


    private var _binding: FragmentEventCommentsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentEventCommentsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var eventId: String? = null
        var eventName: String? = null
        if (arguments != null) {
            eventId = arguments?.getString("eventId")
            eventName = arguments?.getString("eventName")
        }

        binding.textDashboard.text = "No Comments"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}