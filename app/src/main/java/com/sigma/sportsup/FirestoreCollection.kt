package com.sigma.sportsup

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreCollection {

    private val db = Firebase.firestore

    val games = db.collection("games")
    val users = db.collection("users")
    val venues = db.collection("venues")

    val getGames = games.addSnapshotListener { value, error ->  }
}