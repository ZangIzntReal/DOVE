package com.example.dove.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.dove.R

class AddContactDialogFragment : DialogFragment() {

    interface AddContactDialogListener {
        fun onDialogPositiveClick(email: String)
    }

    var listener: AddContactDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_contact, null)
            val editText = view.findViewById<EditText>(R.id.editTextEmail)
            val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

            btnSubmit.setOnClickListener {
                listener?.onDialogPositiveClick(editText.text.toString())
                dialog?.dismiss()
            }

            builder.setView(view)
                .setNegativeButton("Cancel") { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}