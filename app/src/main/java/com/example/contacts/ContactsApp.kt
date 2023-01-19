package com.example.contacts

import android.app.Application

class ContactsApp: Application() {
    val db by lazy {
        ContactDatabase.getInstance(this)
    }
}