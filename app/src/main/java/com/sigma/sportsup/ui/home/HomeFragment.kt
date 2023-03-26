package com.sigma.sportsup.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sigma.sportsup.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

       // val textView: TextView = binding.textHome
        val gameRecycler = binding.gamesRecyclerview
        val sessionRecycler = binding.sessionsRecyclerview

        sessionRecycler.setHasFixedSize(true)
        gameRecycler.setHasFixedSize(true)

        homeViewModel.games.observe(viewLifecycleOwner) {
            gameRecycler.adapter  = GamesItemAdapter(context = requireContext(), it)
            gameRecycler.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        }

        homeViewModel.sessions.observe(viewLifecycleOwner){
            sessionRecycler.adapter = SessionsItemAdapter(requireContext(), it)
            sessionRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}