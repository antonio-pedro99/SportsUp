package com.sigma.sportsup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sigma.sportsup.ui.event.EventDetailsFragment

class EventDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        supportActionBar?.hide()
        if (intent.hasExtra("eventId") && intent.hasExtra("eventName")){
            val eventId = intent.getStringExtra("eventId")
            val eventName = intent.getStringExtra("eventName")
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val fragmentEventDetails = EventDetailsFragment()
            val args = Bundle()
            args.putString("eventId", eventId)
            args.putString("eventName", eventName)
            fragmentEventDetails.arguments = args
            fragmentTransaction.replace(R.id.fragment_container_event, fragmentEventDetails)
                .commit()
        }
    }
}