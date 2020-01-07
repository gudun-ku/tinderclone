package com.beloushkin.tinderclone.fragments.base

import androidx.fragment.app.Fragment
import com.beloushkin.tinderclone.views.TinderCallback
import com.google.firebase.database.DatabaseReference

open class TinderFragment(): Fragment() {

    protected var callback: TinderCallback? = null
    protected lateinit var userId: String
    protected lateinit var userDatabase: DatabaseReference

    constructor(callback: TinderCallback):this() {
        this.callback = callback
        callback?.let {
            userId = it.getUserId()
            userDatabase = it.getUserDatabase().child(userId)
        }
    }

}