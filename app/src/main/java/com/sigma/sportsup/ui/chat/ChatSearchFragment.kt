package com.sigma.sportsup.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.sigma.sportsup.R

class ChatSearchFragment:Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true);

        Log.d("ChatSearchFragment", "onCreateView: ")
        return  inflater.inflate(R.layout.fragment_search, container, false)
    }

   /* override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_fragment_menu, menu)

        val searchItem = menu.findItem(R.id.search_menu_item)
        val searchView = searchItem.actionView as SearchView
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
        super.onCreateOptionsMenu(menu, inflater)
    }*/
}