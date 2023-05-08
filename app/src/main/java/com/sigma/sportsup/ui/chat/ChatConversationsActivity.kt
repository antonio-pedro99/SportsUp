package com.sigma.sportsup.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.sigma.sportsup.R

class ChatConversationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_conversations)

        val fragment = ChatMessagesFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_chat, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val searchItem = menu?.findItem(R.id.search_menu_item)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text changes here
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("ChatBox", "onOptionsItemSelected: ${item.itemId == com.sigma.sportsup.R.id.search_menu_item}")

        return when (item.itemId) {
           R.id.search_menu_item  -> {
                Log.d("ChatBox", "onOptionsItemSelected: ")
                val fragment = ChatSearchFragment()
                supportFragmentManager.beginTransaction()
                    .replace(com.sigma.sportsup.R.id.fragment_container_chat, fragment)
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}