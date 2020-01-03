package com.beloushkin.tinderclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import android.widget.Toast
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var al = mutableListOf<String>()
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var i = 0
    private var toast: Toast? = null

    private fun makeShortToast(message:String) {
        toast?.cancel()

        toast = Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
        toast?.show()
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
        arrayAdapter = ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al)

        //set the listener and the adapter
        frame.adapter = arrayAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                al.removeAt(0)
                arrayAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                makeShortToast("Left!")
            }

            override fun onRightCardExit(p0: Any?) {
                makeShortToast("Right!")
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
            makeShortToast("Clicked!")
        }
    }
}
