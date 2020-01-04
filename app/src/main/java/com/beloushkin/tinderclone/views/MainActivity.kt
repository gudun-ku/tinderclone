package com.beloushkin.tinderclone.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import android.widget.Toast
import android.widget.ArrayAdapter
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private var al = mutableListOf<String>()
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var i = 0

    override fun makeToast(message: String) {
        super.makeToast(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //add the view via xml or programmatically
        val flingContainer = frame as SwipeFlingAdapterView

        al.add("php")
        al.add("c")
        al.add("python")
        al.add("java")

        //choose your favorite adapter
        arrayAdapter = ArrayAdapter<String>(this,
            R.layout.item,
            R.id.helloText, al)

        //set the listener and the adapter
        frame.adapter = arrayAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                al.removeAt(0)
                arrayAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                makeToast("Left!")
            }

            override fun onRightCardExit(p0: Any?) {
                makeToast("Right!")
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
                al.add("XML $i")
                arrayAdapter?.notifyDataSetChanged()
                Log.d("LIST", "notified")
                i++
            }

            override fun onScroll(p0: Float) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        // Optionally add an OnItemClickListener
        frame.setOnItemClickListener { itemPosition, dataObject ->
            makeToast("Clicked!")
        }
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, MainActivity::class.java)
    }
}
