package com.sigma.sportsup.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.sigma.sportsup.data.Contact
import com.sigma.sportsup.data.UserModel

class UsersRepository(private val context: Context) {
    fun getUsers(): LiveData<List<UserModel>> {
        val contactsLiveData = MutableLiveData<List<UserModel>>()
        //get the users from the users collection
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
        return contactsLiveData
    }

    //get search users by name
    fun searchUsersByName(name: String): LiveData<List<UserModel>> {
        val contactsLiveData = MutableLiveData<List<UserModel>>()
        //get the users from the users collection
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereArrayContains("name", name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
        return contactsLiveData
    }

}

