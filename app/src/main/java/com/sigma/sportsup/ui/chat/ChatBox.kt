package com.sigma.sportsup.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sigma.sportsup.databinding.ActivityChatBinding
import com.sigma.sportsup.databinding.ActivityChatboxBinding

class ChatBox: AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var myAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference

    private lateinit var binding: ActivityChatboxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatboxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = UserAdapter(this,userList)

        userRecyclerView = binding.userRecyclerView

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter=adapter

        databaseRef.child("user").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(myAuth.currentUser?.uid!=currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }

                println(userList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }





}