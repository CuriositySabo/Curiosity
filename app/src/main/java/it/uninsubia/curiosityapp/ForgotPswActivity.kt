package it.uninsubia.curiosityapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityForgotPswBinding
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding

class ForgotPswActivity : AppCompatActivity() {
    private lateinit var layout: ActivityForgotPswBinding
    private lateinit var resetBtn: Button
    private lateinit var etEmail: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        layout = ActivityForgotPswBinding.inflate(layoutInflater)
        setContentView(layout.root)

        progressBar = layout.progressbar
        etEmail = layout.editTextEmail
        resetBtn = layout.resetBtn
        resetBtn.setOnClickListener() {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Inserisci una email"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "É necessario inserire una email esistente"
            etEmail.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.sendPasswordResetEmail(email).addOnCompleteListener() {
            if (it.isSuccessful) {
                Toast.makeText(
                    this,
                    "Controlla la tua email per resettare la password!",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                Toast.makeText(
                    this,
                    "Qualcosa non è risucito. Riprova!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        progressBar.visibility = View.GONE
    }
}