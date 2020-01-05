package com.beloushkin.tinderclone.views

import com.google.firebase.database.DatabaseReference

interface TinderCallback {

    fun onSignout()
    fun getUserId(): String
    fun getUserDatabase(): DatabaseReference
}