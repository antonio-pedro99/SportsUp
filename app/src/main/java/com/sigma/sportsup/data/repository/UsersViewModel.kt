package com.sigma.sportsup.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sigma.sportsup.data.UserModel

class UsersViewModel(private val repository: UsersRepository) : ViewModel() {
    fun getUsers(): LiveData<List<UserModel>> {
        return repository.getUsers( )
    }

    fun searchUsersByName(name: String): LiveData<List<UserModel>> {
        return repository.searchUsersByName(name)
    }
}