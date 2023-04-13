package com.sigma.sportsup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sigma.sportsup.data.UserModel

class UserViewModel:ViewModel() {

    private val usersRef = FirestoreCollection().users

    private val _currentUser = MutableLiveData<UserModel>().apply {
        val authUser = Firebase.auth.currentUser
        usersRef.document(authUser?.uid.toString()).get().addOnSuccessListener { snapshot->
            val user = snapshot?.toObject(UserModel::class.java)?.apply { id = authUser?.uid }
            value = user

        }.addOnFailureListener {
                exception -> {
        }
        }

    }
    val currentUser: MutableLiveData<UserModel> = _currentUser
}