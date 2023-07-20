package ru.netology.nmaps.ui

import android.app.Dialog
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class FirstStartDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Нажмите на объект или место на карте, чтобы добавить их в список планируемых к посещению мест")
                .setPositiveButton("Понятно") { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}