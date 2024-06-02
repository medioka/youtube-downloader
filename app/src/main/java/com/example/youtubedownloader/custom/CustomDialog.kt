package com.example.youtubedownloader.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import com.example.youtubedownloader.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomDialog(private val context: Context) {
    fun confirmationDeleteDialog(
        onDelete: (Boolean) -> Unit
    ) {
        val choicesArray = arrayOf("Delete file from device")
        val booleanArray = booleanArrayOf(false)
        MaterialAlertDialogBuilder(context)
            .setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton("Delete") { _, _ ->
                onDelete(booleanArray[0])
            }
            .setTitle("Delete video")
            .setMultiChoiceItems(choicesArray, booleanArray) { _, position, isChecked ->
                booleanArray[position] = isChecked
            }.show()
    }

    fun createChooserDialog(
        itemList: Array<String>,
        currentItem: String,
        onOkClick: (Int) -> Unit
    ) {
        var currentIndex = itemList.indexOfFirst { it == currentItem }
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.select_theme))
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                onOkClick(currentIndex)
            }
            .setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
            .setSingleChoiceItems(itemList, currentIndex) { _, id -> currentIndex = id }
            .create()
        dialog.show()
    }

    fun confirmationDownloadDialog(
        onClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton("Download") { _, _ ->
                onClick()
            }
            .setTitle("File already exist")
            .show()

    }
}

class LoadingDialog(private val activity: Activity) {
    private var dialog: Dialog? = null

    @SuppressLint("InflateParams")
    fun startDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(activity)
        val inflater = activity.layoutInflater
        dialogBuilder.apply {
            setView(inflater.inflate(R.layout.dialog_loading, null))
            setCancelable(false)
        }
        dialog = dialogBuilder.show()
        dialog?.show()
    }

    fun closeDialog() {
        dialog?.dismiss()
    }
}