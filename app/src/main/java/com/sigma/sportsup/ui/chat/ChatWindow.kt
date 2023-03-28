package com.sigma.sportsup.ui.chat

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.auth.User

class ChatWindow : AppCompatActivity(){
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var myAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
}