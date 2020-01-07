package com.beloushkin.tinderclone.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.DATA_CHATS
import com.beloushkin.tinderclone.data.DATA_MESSAGES
import com.beloushkin.tinderclone.data.Message
import com.beloushkin.tinderclone.data.User
import com.beloushkin.tinderclone.views.adapters.MessagesAdapter
import com.beloushkin.tinderclone.views.base.BaseActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : BaseActivity() {

    private var chatId: String? = null
    private var userId: String? = null
    private var imageUrl: String? = null
    private var otherUserId: String? = null

    private lateinit var chatDatabase: DatabaseReference
    private lateinit var messagesAdapter: MessagesAdapter

    private val chatMessagesListnener = object: ChildEventListener {
        override fun onCancelled(err: DatabaseError) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, childId: String?) {

        }

        override fun onChildChanged(snapshot: DataSnapshot, childId: String?) {

        }

        override fun onChildAdded(snapshot: DataSnapshot, childId: String?) {
            val message = snapshot.getValue(Message::class.java)
            message?.let {
                messagesAdapter.addMessage(it)
                messagesRV.post {
                    messagesRV.smoothScrollToPosition(messagesAdapter.itemCount - 1)
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        with (intent.extras) {
            chatId = this?.getString(PARAM_CHAT_ID)
            userId = this?.getString(PARAM_USER_ID)
            imageUrl = this?.getString(PARAM_IMAGE_URL)
            otherUserId = this?.getString(PARAM_OTHER_USER_ID)
        }

        if (chatId.isNullOrEmpty() ||
            userId.isNullOrEmpty() ||
            imageUrl.isNullOrEmpty() ||
            otherUserId.isNullOrEmpty()) {
            makeToast("Chat Room error!")
            finish()
        }

        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)
        messagesAdapter = MessagesAdapter(ArrayList(), userId!!)
        messagesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = messagesAdapter
        }

        chatDatabase.child(chatId!!).child(DATA_MESSAGES)
            .addChildEventListener(chatMessagesListnener)

        chatDatabase.child(chatId!!).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { value ->
                    val key = value.key
                    val user = value.getValue(User::class.java)
                    if(!key.equals(userId)) {
                        topNameTV.text = user?.name
                        Glide.with(this@ChatActivity)
                            .load(user?.imageUrl)
                            .into(topPhotoIV)

                        topPhotoIV.setOnClickListener{
                            startActivity(UserinfoActivity.newIntent(this@ChatActivity, otherUserId))
                        }
                    }
                }
            }

        })
    }

    fun onSend(v: View) {
        if (messageET.text.toString().isEmpty())
            return

        val message =
            Message(userId, messageET.text.toString(), Calendar.getInstance().time.toString())
        val key = chatDatabase.child(chatId!!).child(DATA_MESSAGES).push().key
        if(!key.isNullOrEmpty()) {
            chatDatabase.child(chatId!!).child(DATA_MESSAGES).child(key).setValue(message)
        }
        messageET.setText("", TextView.BufferType.EDITABLE)

    }

    companion object {

        private val PARAM_CHAT_ID = "chatId"
        private val PARAM_USER_ID = "userId"
        private val PARAM_IMAGE_URL = "imageUrl"
        private val PARAM_OTHER_USER_ID ="otherUserId"

        fun newIntent(context: Context?, chatId: String?, userId: String?,
                    imageUrl: String?, otherUserId: String?): Intent {

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(PARAM_CHAT_ID, chatId)
            intent.putExtra(PARAM_USER_ID, userId)
            intent.putExtra(PARAM_IMAGE_URL, imageUrl)
            intent.putExtra(PARAM_OTHER_USER_ID, otherUserId)
            return intent

        }
    }
}
