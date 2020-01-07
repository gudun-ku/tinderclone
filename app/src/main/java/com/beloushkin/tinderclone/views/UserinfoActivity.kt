package com.beloushkin.tinderclone.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.DATA_USERS
import com.beloushkin.tinderclone.data.User
import com.beloushkin.tinderclone.views.base.BaseActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_info.*

class UserinfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val userId = intent.extras?.getString(PARAM_USER_ID, "") ?: ""
        if (userId.isEmpty()) {
            finish()
        }

        val userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        userDatabase.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    userInfoName.text = it.name
                    userInfoAge.text = it.age
                    if (it.imageUrl != null) {
                        Glide.with(this@UserinfoActivity)
                            .load(it.imageUrl)
                            .into(userInfoIV)
                    }
                }
            }
        })

    }

    companion object {
        private val PARAM_USER_ID = "userId"
        fun newIntent(context: Context?, userId: String? ): Intent {
            val intent = Intent(context, UserinfoActivity::class.java)
            intent.putExtra(PARAM_USER_ID, userId)
            return intent
        }
    }
}
