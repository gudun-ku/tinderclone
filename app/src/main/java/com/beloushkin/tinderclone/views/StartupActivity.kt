package com.beloushkin.tinderclone.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.views.base.BaseActivity

class StartupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }

    fun onLogin(v: View) {
        startActivity(LoginActivity.newIntent(this))
    }

    fun onSignup(v: View) {
        startActivity(SignupActivity.newIntent(this))
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, StartupActivity::class.java)
    }
}
