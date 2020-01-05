package com.beloushkin.tinderclone.fragments.base

import androidx.fragment.app.Fragment
import com.beloushkin.tinderclone.views.TinderCallback

open class TinderFragment(): Fragment() {

    protected var callback: TinderCallback? = null

    constructor(callback: TinderCallback):this() {
        this.callback = callback
    }
}