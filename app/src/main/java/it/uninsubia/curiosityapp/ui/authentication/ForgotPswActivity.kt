package it.uninsubia.curiosityapp.ui.authentication

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityForgotPswBinding

// activity che gestisce il reset password di un account già registrato
class ForgotPswActivity : AppCompatActivity() {
    private lateinit var layout: ActivityForgotPswBinding
    private lateinit var resetBtn: Button
    private lateinit var etEmail: com.google.android.material.textfield.TextInputEditText
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    // inizializzo alcuni oggetti utili
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

    // il bottone invia alla email dell'utente registrato una mail contente il link per modificare la password
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

        // Metodo della librearia di firebase che fa tutto in automatico
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "Controlla la tua email per resettare la password!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Qualcosa non è risucito. Riprova!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        progressBar.visibility = View.GONE
    }
}