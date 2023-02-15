package com.soft.zb.accidentwarning.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soft.zb.accidentwarning.R

class AboutFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_about, null)
        return view

//        return super.onCreateView(inflater, container, savedInstanceState)
    }
}