package com.sigma.sportsup.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sigma.sportsup.data.Contact

class ContactsRepository(private val context: Context) {
    fun loadContactsFromPhone(): LiveData<List<Contact>> {
        val contactsLiveData = MutableLiveData<List<Contact>>()

        val contactsList = mutableListOf<String>()
        val contentResolver = context.contentResolver

        val selection = "${ContactsContract.CommonDataKinds.Email.DATA} LIKE ?"

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        // Load contacts from phone
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        phoneCursor?.use {
            val contacts = mutableListOf<Contact>()
            val nameColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME)
            val emailColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            val phoneColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameColumnIndex)
                val email = it.getString(emailColumnIndex)

                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?",
                    arrayOf(name),
                    null
                )

                val phone = if (phoneCursor != null && phoneCursor.moveToNext()) {
                    val columnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    if (columnIndex >= 0) {
                        phoneCursor.getString(columnIndex)
                    } else {
                        ""
                    }
                } else {
                    ""
                }

                phoneCursor?.close()

                val contact = Contact(name, email, phone)
                contacts.add(contact)

                Log.d("Contact", contact.toString())
            }
        }




        return contactsLiveData
    }

}

