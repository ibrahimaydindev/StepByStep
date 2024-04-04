package com.fit.stepbystep.activity

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.fit.stepbystep.R
import com.google.android.material.button.MaterialButton


class Dialog : DialogFragment() {
    private val tag = "dialog"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var context: Context
    private lateinit var button : MaterialButton


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val window = dialog.window

        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.fragment_dialog)

        initView(dialog)

        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    private fun show(manager: FragmentManager){
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commit()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

    private fun dismissDialog(manager:FragmentManager) {
        try {
            val ft = manager.beginTransaction()
            ft.remove(this)
            ft.commit()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun initView(dialog : Dialog){
        nameEditText = dialog.findViewById(R.id.editTextTextPersonName)
        surnameEditText = dialog.findViewById(R.id.editTextTextPersonSurname)
        heightEditText = dialog.findViewById(R.id.editTextTextPersonHeight)
        weightEditText = dialog.findViewById(R.id.editTextTextPersonWeight)

        heightEditText.inputType = InputType.TYPE_CLASS_NUMBER
        weightEditText.inputType = InputType.TYPE_CLASS_NUMBER

        sharedPreferences = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE)

        nameEditText.setText(sharedPreferences.getString("name", ""))
        surnameEditText.setText(sharedPreferences.getString("year", ""))
        heightEditText.setText(sharedPreferences.getString("height", ""))
        weightEditText.setText(sharedPreferences.getString("weight", ""))

        button = dialog.findViewById(R.id.popupButton)

        button.setOnClickListener {
            val heightValue = heightEditText.text.toString()
            val weightValue = weightEditText.text.toString()

            if (heightValue.isNotEmpty() && weightValue.isNotEmpty()) {
                saveData()
                dismissDialog(requireFragmentManager())
            } else {
                Toast.makeText(requireContext(), "Lütfen boy ve kilo alanlarını doldurunuz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData() {
        val editor = sharedPreferences.edit()
        editor.putString("name", nameEditText.text.toString())
        editor.putString("year", surnameEditText.text.toString())
        editor.putString("height", heightEditText.text.toString())
        editor.putString("weight", weightEditText.text.toString())
        editor.apply()
    }

}