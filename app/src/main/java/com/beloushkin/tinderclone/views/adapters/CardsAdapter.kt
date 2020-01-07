package com.beloushkin.tinderclone.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.User
import com.beloushkin.tinderclone.views.UserinfoActivity
import com.bumptech.glide.Glide

class CardsAdapter(context: Context, resourceId: Int, users: List<User>)
    : ArrayAdapter<User>(context, resourceId, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var user = getItem(position)
        var finalView = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.item ,parent, false)

        user?.run {
            var name = finalView.findViewById<TextView>(R.id.nameTV)
            var image = finalView.findViewById<ImageView>(R.id.photoIV)
            var userInfo = finalView.findViewById<LinearLayout>(R.id.userInfoLayout)

            name.text = "${this.name}, ${this.age}"
            Glide.with(context)
                .load(this.imageUrl)
                .into(image)

            userInfo.setOnClickListener {
                finalView.context.startActivity(
                    UserinfoActivity.newIntent(finalView.context, this.uid)
                )
            }
        }

        return finalView
    }
}