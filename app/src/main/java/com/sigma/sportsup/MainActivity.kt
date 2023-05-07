package com.sigma.sportsup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                R.id.navigation_home, R.id.navigation_events, R.id.navigation_profile
            )
        )

        val fcmServiceIntent = Intent(this, MyFirebaseMessagingService::class.java)
        startService(fcmServiceIntent)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        requestSinglePermissionLauncher(
           {
                Log.d("Home", ": denied")
            },
            {
                Toast.makeText(this, "Permission denied: Please all the app not send notifications", Toast.LENGTH_SHORT).show()
            }
        ).launch(android.Manifest.permission.POST_NOTIFICATIONS)

    }

     private  val requestMultiplePermissionLauncher = {onResult:(Map<String, Boolean>)->Unit->
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onResult(result)
        }
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


    fun getRequestMultiplePermissionLauncher(onResult:(Map<String, Boolean>)->Unit) = requestMultiplePermissionLauncher(onResult)
    fun getRequestSinglePermissionLauncher(onGranted:()->Unit , onDenied: () -> Unit) = requestSinglePermissionLauncher(onGranted, onDenied)
}