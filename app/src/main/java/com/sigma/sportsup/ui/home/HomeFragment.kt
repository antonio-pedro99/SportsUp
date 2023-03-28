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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val chat_but:Button = binding.chatBut

       // val textView: TextView = binding.textHome
        val gameRecycler = binding.gamesRecyclerview
        val sessionRecycler = binding.sessionsRecyclerview

        sessionRecycler.setHasFixedSize(true)
        gameRecycler.setHasFixedSize(true)

        homeViewModel.games.observe(viewLifecycleOwner) {
            gameRecycler.adapter  = GamesItemAdapter(context = requireContext(), it!!)
            gameRecycler.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        }

        homeViewModel.sessions.observe(viewLifecycleOwner){
            sessionRecycler.adapter = SessionsItemAdapter(requireContext(), it)
            sessionRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        chat_but.setOnClickListener(View.OnClickListener {
            val myIntent = Intent(this@HomeFragment.context, ChatBox::class.java)

            this@HomeFragment.startActivity(myIntent)
            println("this is working!")
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}