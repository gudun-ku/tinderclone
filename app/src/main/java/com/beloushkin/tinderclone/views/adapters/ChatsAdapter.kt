package com.beloushkin.tinderclone.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.Chat
import com.beloushkin.tinderclone.views.ChatActivity
import com.bumptech.glide.Glide

class ChatsAdapter(private var chats: ArrayList<Chat>)
    :RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChatsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false))

    override fun getItemCount() = chats.size

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    fun addElement(chat: Chat) {
        chats.add(chat)
        notifyDataSetChanged()
    }

    class ChatsViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        private var layout = view.findViewById<View>(R.id.chatLayout)
        private var image = view.findViewById<ImageView>(R.id.chatPictureIV)
        private var name = view.findViewById<TextView>(R.id.chatNameTV)

        fun bind(chat: Chat) {
            name.text = chat.name
            if (image !=null ) {
                Glide.with(view)
                    .load(chat.imageUrl)
                    .into(image)
            }

            layout.setOnClickListener {
                val intent = ChatActivity.newIntent(view.context, chat.chatId,
                    chat.userId, chat.imageUrl, chat.otherUserId)

                view.context.startActivity(intent)
            }

        }

    }
}