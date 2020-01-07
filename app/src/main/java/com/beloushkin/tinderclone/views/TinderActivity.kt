package com.beloushkin.tinderclone.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.data.DATA_PROFILE_IMAGE
import com.beloushkin.tinderclone.data.DATA_USERS
import com.beloushkin.tinderclone.fragments.MatchesFragment
import com.beloushkin.tinderclone.fragments.ProfileFragment
import com.beloushkin.tinderclone.fragments.SwipeFragment
import com.beloushkin.tinderclone.views.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_tinder.*
import android.graphics.ImageDecoder
import android.os.Build
import com.beloushkin.tinderclone.data.DATA_CHATS
import java.io.ByteArrayOutputStream
import java.io.IOException


const val REQUEST_CODE_PHOTO = 1234

class TinderActivity : BaseActivity(), TinderCallback {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference

    lateinit var profileFragment: ProfileFragment
    lateinit var swipeFragment: SwipeFragment
    lateinit var matchesFragment: MatchesFragment

    lateinit var profileTab: TabLayout.Tab
    lateinit var swipeTab: TabLayout.Tab
    lateinit var matchesTab: TabLayout.Tab

    private var resultImageUri: Uri?  = null

    override fun makeToast(message: String) {
        super.makeToast(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tinder)

        if(userId.isNullOrEmpty()) {
            onSignout()
        }

        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        makeNavigationTabs()
        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab) {
                    profileTab -> {
                        if (!::profileFragment.isInitialized) profileFragment = ProfileFragment(this@TinderActivity)
                        replaceFragment(profileFragment)
                    }
                    swipeTab -> {
                        if (!::swipeFragment.isInitialized) swipeFragment = SwipeFragment(this@TinderActivity)
                        replaceFragment(swipeFragment)
                    }
                    matchesTab -> {
                        if (!::matchesFragment.isInitialized) matchesFragment = MatchesFragment(this@TinderActivity)
                        replaceFragment(matchesFragment)
                    }
                }
            }
        })

        profileTab.select()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun getUserId() = userId!!

    override fun getUserDatabase() = userDatabase

    override fun getChatDatabase() = chatDatabase

    override fun profileComplete() {
        swipeTab?.select()
    }

    override fun startActivityForPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            resultImageUri = data?.data
            storeImage()
        }
    }

    fun storeImage() {
        if(resultImageUri != null && userId != null) {
            val filePath = FirebaseStorage.getInstance().reference
                                        .child(DATA_PROFILE_IMAGE).child(userId)
            var bitmap:Bitmap? = null
            try {
                resultImageUri?.let {imageUri ->
                    if(Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            imageUri
                        )

                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                        bitmap = ImageDecoder.decodeBitmap(source)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }


            bitmap?.run {
                val baos = ByteArrayOutputStream()
                compress(Bitmap.CompressFormat.JPEG, 20, baos)
                val data = baos.toByteArray()

                val uploadTask = filePath.putBytes(data)
                uploadTask.addOnFailureListener{ e -> e.printStackTrace()}
                uploadTask.addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener { uri ->
                        profileFragment.updateImageUri(uri.toString())
                    }
                        .addOnFailureListener {e -> e.printStackTrace()}
                }
            }
        }
    }

    private fun makeNavigationTabs() {
        profileTab = navigationTabs.newTab()
        swipeTab = navigationTabs.newTab()
        matchesTab = navigationTabs.newTab()

        profileTab.icon = ContextCompat.getDrawable(this, R.drawable.tab_profile)
        swipeTab.icon = ContextCompat.getDrawable(this, R.drawable.tab_swipe)
        matchesTab.icon = ContextCompat.getDrawable(this, R.drawable.tab_matches)

        navigationTabs.addTab(profileTab)
        navigationTabs.addTab(swipeTab)
        navigationTabs.addTab(matchesTab)
    }


    companion object {
        fun newIntent(context: Context?) = Intent(context, TinderActivity::class.java)
    }
}
