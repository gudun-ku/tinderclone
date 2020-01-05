package com.beloushkin.tinderclone.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.fragments.base.TinderFragment
import com.beloushkin.tinderclone.views.TinderCallback

class SwipeFragment : TinderFragment {

    constructor(callback: TinderCallback): super(callback)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }


}
