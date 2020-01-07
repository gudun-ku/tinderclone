package com.beloushkin.tinderclone.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.*
import com.beloushkin.tinderclone.fragments.base.TinderFragment
import com.beloushkin.tinderclone.views.TinderCallback
import com.beloushkin.tinderclone.views.adapters.CardsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_swipe.*

class SwipeFragment : TinderFragment {

    private var cardsAdapter: ArrayAdapter<User>? = null
    private var rowItems = ArrayList<User>()
    private var preferredGender: String? = null
    private var userName: String? = null
    private var userImageUrl: String? = null
    private var chatDatabase: DatabaseReference

    constructor(callback: TinderCallback) : super(callback) {
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                preferredGender = user?.preferredGender
                userName = user?.name
                userImageUrl = user?.imageUrl
                populateItems()
            }
        })

        cardsAdapter = CardsAdapter(context!!, R.layout.item, rowItems)

        frame.adapter = cardsAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                rowItems.removeAt(0)
                cardsAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(data: Any?) {
                var user = data as User
                userDatabase.child(user.uid.toString()).child(DATA_SWIPES_LEFT).child(userId)
                    .setValue(true)
            }

            override fun onRightCardExit(data: Any?) {
                val selectedUser = data as User
                val selectedUserId = selectedUser.uid
                if (!selectedUserId.isNullOrEmpty()) {
                    // check if user handled as swiped right
                    userDatabase.child(userId).child(DATA_SWIPES_RIGHT)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(err: DatabaseError) {
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChild(selectedUserId)) {
                                    callback?.makeToast("Match!")

                                    // create chat
                                    val chatKey = chatDatabase.push().key

                                    chatKey?.let {
                                        userDatabase.child(userId).child(DATA_SWIPES_RIGHT)
                                            .child(selectedUserId).removeValue()
                                        // set chatkey for selected matches
                                        userDatabase.child(userId).child(DATA_MATCHES)
                                            .child(selectedUserId).setValue(it)
                                        userDatabase.child(selectedUserId).child(DATA_MATCHES)
                                            .child(userId).setValue(it)

                                        chatDatabase.child(it).child(userId).child(DATA_NAME)
                                            .setValue(userName)
                                        chatDatabase.child(it).child(userId).child(DATA_IMAGE_URL)
                                            .setValue(userImageUrl)

                                        chatDatabase.child(it).child(selectedUserId)
                                            .child(DATA_NAME).setValue(selectedUser.name)
                                        chatDatabase.child(it).child(selectedUserId)
                                            .child(DATA_IMAGE_URL).setValue(selectedUser.imageUrl)
                                    }


                                } else {
                                    userDatabase.child(selectedUserId).child(DATA_SWIPES_RIGHT)
                                        .child(userId).setValue(true)
                                }
                            }
                        })
                }
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onScroll(p0: Float) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        frame.setOnItemClickListener { _, _ -> }

        likeButton.setOnClickListener {
            if (rowItems.isNotEmpty()) {
                frame.topCardListener.selectRight()
            }
        }

        dislikeButton.setOnClickListener {
            if (rowItems.isNotEmpty()) {
                frame.topCardListener.selectLeft()
            }
        }
    }

    fun populateItems() {
        noUsersLayout.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE

        val cardsQuery = userDatabase.orderByChild(DATA_GENDER).equalTo(preferredGender)
        cardsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        var showUser = true
                        if (child.child(DATA_SWIPES_LEFT).hasChild(userId) ||
                            child.child(DATA_SWIPES_RIGHT).hasChild(userId) ||
                            child.child(DATA_MATCHES).hasChild(userId)
                        ) {
                            showUser = false
                        }
                        if (showUser) {
                            rowItems.add(user)
                            cardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                progressLayout.visibility = View.GONE

                if (rowItems.isEmpty()) {
                    noUsersLayout.visibility = View.VISIBLE
                }
            }
        })

    }


}
