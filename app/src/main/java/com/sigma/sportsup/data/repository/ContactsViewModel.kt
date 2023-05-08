package com.sigma.sportsup.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sigma.sportsup.data.Contact

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {
    fun getContactsFromEmailDomain(): LiveData<List<Contact>> {
        return repository.loadContactsFromPhone( )
    }
}
