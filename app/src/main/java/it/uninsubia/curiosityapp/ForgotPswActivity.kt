package it.uninsubia.curiosityapp

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityForgotPswBinding

class ForgotPswActivity : AppCompatActivity() {
    private lateinit var layout: ActivityForgotPswBinding
    private lateinit var resetBtn: Button
    private lateinit var etEmail: com.google.android.material.textfield.TextInputEditText
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityForgotPswBinding.inflate(layoutInflater)
        setContentView(layout.root)
        auth = FirebaseAuth.getInstance()

        progressBar = layout.progressbar
        etEmail = layout.editTextEmail
        resetBtn = layout.resetBtn
        resetBtn.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Inserisci una mail"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "É necessario inserire una email esistente"
            etEmail.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Utility.createSnackbar(
                    "Controlla la tua email per resettare la password!",
                    this.findViewById(R.id.fgt_pass_layout),
                    applicationContext)
            } else {
                Utility.createSnackbar(
                    "Qualcosa non è risucito. Riprova!",
                    this.findViewById(R.id.fgt_pass_layout),
                    applicationContext)
            }
        }
        progressBar.visibility = View.GONE
    }
}