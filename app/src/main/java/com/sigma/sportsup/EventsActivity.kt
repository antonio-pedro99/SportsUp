package com.sigma.sportsup

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sigma.sportsup.system.services.MyFirebaseMessagingService
import com.sigma.sportsup.ui.event.EventDetailsFragment
import com.sigma.sportsup.ui.events.EventFragment

class EventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        // Get a reference to the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_events) as NavHostFragment

        // Get the NavController
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        if ( intent.hasExtra("gameName")){
            val gameName = intent.getStringExtra("gameName")
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val fragmentEvent = EventFragment()
            val args = Bundle()
            args.putString("gameName", gameName)
            fragmentEvent.arguments = args
            fragmentTransaction.replace(R.id.fragment_events, fragmentEvent)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}