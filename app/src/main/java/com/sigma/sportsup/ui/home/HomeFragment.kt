package com.sigma.sportsup.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.protobuf.Empty
import com.sigma.sportsup.MainActivity
import com.sigma.sportsup.R
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.databinding.FragmentHomeBinding
import com.sigma.sportsup.ui.chat.ChatBox
import com.sigma.sportsup.ui.events.EventsViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var handler: ActivityResultLauncher<String>
    private lateinit var multiplePermissionHandler : ActivityResultLauncher<Array<String>>
    private lateinit var launcher: ActivityResultLauncher<Intent>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)
        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gameRecycler = binding.gamesRecyclerview
        val sessionRecycler = binding.sessionsRecyclerview



        handler = requestSinglePermissionLauncher(
            {
                Log.d("Home", ": denied")
            },
            {
                Toast.makeText(requireContext(), "Permission denied: Please all the app not send notifications", Toast.LENGTH_SHORT).show()
            }
        )

        multiplePermissionHandler = requestMultiplePermissionLauncher {

        }

        sessionRecycler.setHasFixedSize(true)
        gameRecycler.setHasFixedSize(true)

        homeViewModel.games.observe(viewLifecycleOwner) {
            gameRecycler.adapter = GamesItemAdapter(context = requireContext(), it!!)
            gameRecycler.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        }

        homeViewModel.sessions.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                sessionRecycler.adapter = SessionsItemAdapter(requireContext(), it, { index ->

                }, {

                })
                sessionRecycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
        return when (item.itemId) {
            R.id.chat_menu -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    val myIntent = Intent(this@HomeFragment.context, ChatBox::class.java)
                    this@HomeFragment.startActivity(myIntent)
                }else {
                   multiplePermissionHandler.launch(
                       arrayOf(
                           Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS
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

    private  val requestMultiplePermissionLauncher = {onResult:(Map<String, Boolean>)->Unit->
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onResult(result)
        }
    }



}