package com.example.expensetracker.ui

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar

fun Activity.toast(text:String) {
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}
fun Fragment.toast(text: String){
    Toast.makeText(requireContext(),text,Toast.LENGTH_SHORT).show()

}

fun Activity.snackBar(text:String,view:View){
    Snackbar.make(this,view,text,Snackbar.LENGTH_SHORT)
        .setAnimationMode(ANIMATION_MODE_SLIDE)
        .show()
}
fun Fragment.snackBar(text:String){
    Snackbar.make(requireContext(),requireView(),text,Snackbar.LENGTH_SHORT)
        .setAnimationMode(ANIMATION_MODE_SLIDE)
        .show()
}