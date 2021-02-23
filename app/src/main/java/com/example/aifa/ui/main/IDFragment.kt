package com.example.aifa.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.aifa.Repository
import com.example.aifa.R
import com.example.aifa.loadFund

class IDFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_id, null)
        builder.setView(view)
            .setPositiveButton("SAVE") { _, _ ->
                val id = view.findViewById<EditText>(R.id.ID).text.toString()
                if (id.length != 6) {
                    Toast.makeText(context, "id.length must equal to 6", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val repository = Repository.get()
                    repository.getLiveFund(id).observe(
                        this, {
                            if (it != null) {
                                Toast.makeText(
                                    context,
                                    "${it.name} already existed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                class FundAsync : AsyncTask<String, Unit, Unit>() {
                                    override fun doInBackground(vararg params: String) {
                                        val result =
                                            loadFund(requireContext(), params[0])
                                        if (result != null) {
                                            repository.addFund(result)
                                        } else {
                                            Toast.makeText(context, "Exception", Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }
                                }
                                FundAsync().execute(id)
                            }
                        }
                    )
                }
            }
        return builder.create()
    }
}
