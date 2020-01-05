package com.beloushkin.tinderclone.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.views.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if (user != null) {
            startActivity(TinderActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onLogin(v: View) {
        if (!emailET.text.toString().isNullOrEmpty() && !passwordET.text.toString().isNullOrEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        makeToast("Login error ${task.exception?.localizedMessage}")
                    } else {
                    //  val email = emailET.text.toString()
                    //  val userId = firebaseAuth.currentUser?.uid ?: ""
                    //  val user = User(
                    //    userId, "", "", email, "", "", ""
                    //  )
                    //  firebaseDatabase.child(DATA_USERS).child(userId).setValue(user)
                    }
                }
        }

    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, LoginActivity::class.java)
    }

}
