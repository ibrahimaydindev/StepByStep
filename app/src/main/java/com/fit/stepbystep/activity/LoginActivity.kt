package com.fit.stepbystep.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.fit.stepbystep.R
import com.fit.stepbystep.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            signIn()
        }
    }

    /**
     * Starts the sign in process
     */
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * objects
     */
    companion object {
        private const val RC_SIGN_IN = 9001
        private const val RC_FITNESS_PERMISSIONS = 9002
    }

    /**
     * Accesses the fitness API
     */
    private fun accessFitnessAPI() {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions
            )
        ) {
            GoogleSignIn.requestPermissions(
                this,
                RC_FITNESS_PERMISSIONS,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions
            )
        } else {
            Intent(this, HistoryActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    /**
     * Handles the result of the sign in process
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                accessFitnessAPI()
            } else {
                Toast.makeText(this, "Google hesabına giriş yapmadınız", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(this)
                    .setTitle("Uyarı")
                    .setMessage("Uygulamayı kullanabilmek için Google hesabınıza giriş yapmalısınız.")
                    .setPositiveButton("Tamam") { dialog, _ ->
                        dialog.dismiss()
                        signIn()
                    }
                    .setNegativeButton("Çıkış") { _, _ ->
                        finish()
                    }
                    .show()
            }
        }
    }
}