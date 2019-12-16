package com.kaho.app.Tools

import android.view.View
import com.google.android.material.snackbar.Snackbar

class ExtraMethods {
    fun onSNACK(view: View){
        //Snackbar(view)
        val snackbar = Snackbar.make(view, "Replace with your own action",
                Snackbar.LENGTH_LONG).show()
        /*snackbar.setActionTextColor(Color.BLUE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.LTGRAY)
        val textView =
                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.BLUE)
        textView.textSize = 28f*/
       // snackbar.show()
    }


}