package com.beloushkin.tinderclone.views.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    var toast: Toast? = null

    open fun makeToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this,message, Toast.LENGTH_SHORT)
        toast?.show()
    }

}
