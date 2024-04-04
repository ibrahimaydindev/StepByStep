package com.fit.stepbystep.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fit.stepbystep.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        /**
         * Set the values from shared preferences
         */
        binding.backBtn.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        /**
         * Set the values from shared preferences
         */
        binding.editBtn.setOnClickListener {
            val dialog = Dialog()
            dialog.show(supportFragmentManager, "dialog")
        }

        /**
         * Set the values from shared preferences
         */
        binding.heartButton2.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        /**
         * Set the values from shared preferences
         */
        binding.profileButton2.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.txtNameValue.text = sharedPref.getString("name", "")
        binding.txtYearValue.text = sharedPref.getString("year", 0.toString())
        binding.txtHeightValue.text = sharedPref.getString("height", 0.toString())
        binding.txtWeightValue.text = sharedPref.getString("weight", 0.toString())

        binding.txtIndeksValue.text = calculateIndeks().toString()
    }

    /**
     * Calculate the indeks
     */
    private fun calculateIndeks(): Double {
        val height = sharedPref.getString("height", 0.toString())!!.toInt()
        val weight = sharedPref.getString("weight", 0.toString())!!.toInt()
        return weight / (height * height).toDouble()
    }
}