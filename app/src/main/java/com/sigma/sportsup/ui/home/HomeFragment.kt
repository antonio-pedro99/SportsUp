package com.sigma.sportsup.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sigma.sportsup.R
import com.sigma.sportsup.databinding.FragmentHomeBinding
import com.sigma.sportsup.ui.chat.ChatBox


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gameRecycler = binding.gamesRecyclerview
        val sessionRecycler = binding.sessionsRecyclerview

        sessionRecycler.setHasFixedSize(true)
        gameRecycler.setHasFixedSize(true)

        homeViewModel.games.observe(viewLifecycleOwner) {
            gameRecycler.adapter  = GamesItemAdapter(context = requireContext(), it!!)
            gameRecycler.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        }

        homeViewModel.sessions.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                sessionRecycler.adapter = SessionsItemAdapter(requireContext(), it)
                sessionRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.txtNoOngoingEvent.visibility = View.INVISIBLE
            } else {
                binding.txtNoOngoingEvent.visibility = View.VISIBLE
            }
        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.chat_menu ->{
                val myIntent = Intent(this@HomeFragment.context, ChatBox::class.java)
                this@HomeFragment.startActivity(myIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}