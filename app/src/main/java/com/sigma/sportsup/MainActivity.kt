package com.sigma.sportsup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.sigma.sportsup.databinding.ActivityMainBinding
import com.sigma.sportsup.system.services.MyFirebaseMessagingService


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_events, R.id.navigation_profile
            )
        )

        val fcmServiceIntent = Intent(this, MyFirebaseMessagingService::class.java)
        startService(fcmServiceIntent)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        requestSinglePermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

    }

    private val requestMultiplePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            Log.d("Home", ": $result")
        }

    private val requestSinglePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _: Boolean ->
        }


    fun getRequestMultiplePermissionLauncher() = requestMultiplePermissionLauncher
    fun getRequestSinglePermissionLauncher() = requestSinglePermissionLauncher
}