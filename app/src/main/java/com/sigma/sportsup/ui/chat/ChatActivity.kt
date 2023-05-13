package com.sigma.sportsup.ui.chat

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.R
import com.google.firebase.messaging.FirebaseMessaging
import com.sigma.sportsup.data.NotificationService
import com.sigma.sportsup.databinding.ActivityChatBinding
import com.sigma.sportsup.databinding.ActivityLoginBinding
import com.sigma.sportsup.databinding.ActivityMainBinding
import com.sigma.sportsup.system.services.MyFirebaseMessagingService
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
    private val notificationService = NotificationService()

    private lateinit var me: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val recieverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().reference
        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        me = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        if (name != null) {
            supportActionBar?.title = name.split("2")[0].capitalize()
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
            //subscribe to topic

            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                FirebaseMessaging.getInstance().subscribeToTopic(senderUid!!)
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
            var token:String?=null;
            //token pf receiver needs to be retrieved form the database
//            databaseRef.child("users").child(recieverUid!!).get().addOnSuccessListener {
//
//            }
            val messageObject = Message(message,senderUid,token)

            databaseRef.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObject).addOnSuccessListener {
                    databaseRef.child("chats").child(recieverRoom!!).child("messages").push()
                        .setValue(messageObject)

                notificationService.sendNotificationToTopic(
                    recieverUid!!,
                    me,
                    message
                )

                }

//            databaseRef.child("users").child(senderUid!!).child("deviceToken").push().s


//            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                if(!task.isSuccessful) {
//                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                    return@OnCompleteListener
//                }
//
//                val token = task.result

                // Log and toast

//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            })
            messageBox.setText("")


        }
    }
}