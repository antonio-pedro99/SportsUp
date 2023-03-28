package com.sigma.sportsup.ui.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.R
import com.sigma.sportsup.databinding.ActivityChatBinding
import com.sigma.sportsup.databinding.ActivityLoginBinding
import com.sigma.sportsup.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity :AppCompatActivity(){
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var databaseRef: DatabaseReference

    var recieverRoom:String? =null
    var senderRoom:String? = null

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val recieverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().getReference()
        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid



        if (name != null) {
            supportActionBar?.title = name.toUpperCase(Locale.ROOT)
        }
        chatRecyclerView = binding.chatRecyclerView
        messageBox = binding.messageBox
        sendButton = binding.sentButton
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter=messageAdapter

        databaseRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        sendButton.setOnClickListener{
            val message = messageBox.text.toString()
            val messageObject = Message(message,senderUid)

            databaseRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    databaseRef.child("chats").child(recieverRoom!!).child("messages").push()
                        .setValue(messageObject)

                }
            messageBox.setText("")


        }
    }
}