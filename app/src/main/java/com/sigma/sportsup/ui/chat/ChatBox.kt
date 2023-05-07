package com.sigma.sportsup.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
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
        databaseRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        val contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        val contactEmailIds = mutableListOf<String>()
        if (contactsCursor != null && contactsCursor.moveToFirst()) {
            do {
                val contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))
                val emailsCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(contactId), null)
                while (emailsCursor?.moveToNext() == true) {
                    val email = emailsCursor.getString(emailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    contactEmailIds.add(email)
                }
                emailsCursor?.close()
            } while (contactsCursor.moveToNext())
        }
        contactsCursor?.close()
        println("here we go:")
        println(contactEmailIds)

        userRecyclerView = binding.userRecyclerView

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter=adapter

        databaseRef.child("user").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
//                    println(myAuth.currentUser)


                    if(myAuth.currentUser?.uid!=currentUser?.uid && contactEmailIds.contains(currentUser?.email)){
                        userList.add(currentUser!!)
                    }
                }
                println("hello hello ji")
                println(userList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }





}