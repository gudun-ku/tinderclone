package com.beloushkin.tinderclone.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.Chat
import com.beloushkin.tinderclone.data.DATA_MATCHES
import com.beloushkin.tinderclone.data.User
import com.beloushkin.tinderclone.fragments.base.TinderFragment
import com.beloushkin.tinderclone.views.TinderCallback
import com.beloushkin.tinderclone.views.adapters.ChatsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_matches.*

class MatchesFragment : TinderFragment {

    private lateinit var chatDatabase:DatabaseReference
    private val chatsAdapter = ChatsAdapter(ArrayList())

    constructor(callback: TinderCallback): super(callback) {
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()

        fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter

        }
    }

    fun fetchData() {
        userDatabase.child(userId).child(DATA_MATCHES).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    val matchId = child.key
                    val chatId = child.value.toString()
                    if (!matchId.isNullOrEmpty()) {
                        userDatabase.child(matchId).addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onCancelled(err: DatabaseError) {
                            }

                            override fun onDataChange(usersnapshot: DataSnapshot) {
                                val user = usersnapshot.getValue(User::class.java)
                                user?.run {
                                    val chat = Chat(userId, chatId, uid, name, imageUrl)
                                    chatsAdapter.addElement(chat)
                                }
                            }
                        })

                    }
                }
            }
        })
    }

}
