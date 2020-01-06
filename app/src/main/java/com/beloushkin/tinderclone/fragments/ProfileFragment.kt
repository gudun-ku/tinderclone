package com.beloushkin.tinderclone.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.*
import com.beloushkin.tinderclone.fragments.base.TinderFragment
import com.beloushkin.tinderclone.views.TinderCallback
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : TinderFragment {

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference

    constructor(callback: TinderCallback): super(callback) {
        userId = callback.getUserId()
        userDatabase = callback.getUserDatabase().child(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressLayout.setOnTouchListener { _, _ ->  true }

        populateInfo()

        photoIV.setOnClickListener { callback?.startActivityForPhoto() }

        applyButton.setOnClickListener { onApply() }
        signoutButton.setOnClickListener { callback?.onSignout() }
    }

    fun populateInfo() {
        progressLayout.visibility = View.VISIBLE
        userDatabase.addListenerForSingleValueEvent( object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (isAdded) {
                    val user = snapshot.getValue(User::class.java)

                    user?.run {
                        nameET.setText(name, TextView.BufferType.EDITABLE)
                        emailET.setText(email, TextView.BufferType.EDITABLE)
                        ageET.setText(age, TextView.BufferType.EDITABLE)

                        if (gender == GENDER_MALE)
                            radioMan1.isChecked = true

                        if (gender == GENDER_FEMALE)
                            radioWoman1.isChecked = true

                        if (preferredGender == GENDER_MALE)
                            radioMan2.isChecked = true

                        if (preferredGender == GENDER_FEMALE)
                            radioWoman2.isChecked = true

                        if (!imageUrl.isNullOrEmpty())
                            populateImage(imageUrl)
                    }
                    progressLayout.visibility = View.GONE
                }
            }
        })
    }

    fun onApply() {
        if(
            nameET.text.toString().isNullOrEmpty() ||
            emailET.text.toString().isNullOrEmpty() ||
            radioGroup1.checkedRadioButtonId == -1 ||
            radioGroup2.checkedRadioButtonId == -1
        ) {
            callback?.makeToast(getString(R.string.error_profile_incomplete))
            return
        }

        val name = nameET.text.toString()
        val email = emailET.text.toString()
        val age = ageET.text.toString()
        val gender = if (radioMan1.isChecked) GENDER_MALE else GENDER_FEMALE
        val preferredGender = if (radioMan2.isChecked) GENDER_MALE else GENDER_FEMALE

        with (userDatabase) {
            child(DATA_NAME).setValue(name)
            child(DATA_EMAIL).setValue(email)
            child(DATA_AGE).setValue(age)
            child(DATA_GENDER).setValue(gender)
            child(DATA_GENDER_PREFERENCE).setValue(preferredGender)
        }

        callback?.profileComplete()

    }

    fun updateImageUri(uri: String) {
        userDatabase.child(DATA_IMAGE_URL).setValue(uri)
        // load image to imageview with Glide
        populateImage(uri)
    }

    fun populateImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(photoIV)
    }


}
