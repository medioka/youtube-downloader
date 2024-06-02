package com.example.youtubedownloader.core.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.example.youtubedownloader.settings.SettingsActivity
import com.google.android.material.snackbar.Snackbar

class DialogUtils(private val context: Context) {
    fun createSnackBar(view: View, anchorView: View? = null) {
        val snackBar = Snackbar.make(view, "Update module failed", Snackbar.LENGTH_LONG)
            .setAction("Update") {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            }
            .setAnchorView(anchorView)
        snackBar.show()
    }

    fun createToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}