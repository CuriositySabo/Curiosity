package it.uninsubia.curiosityapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var tvForgot: TextView
    private lateinit var layout: ActivityLoginBinding

    private lateinit var button: Button
    private lateinit var tvRegistrati: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        if(auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        layout = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(layout.root)

        etEmail = layout.editTextEmail
        etPassword = layout.editTextPassword
        progressBar = layout.progressbar

        tvForgot = layout.forgotTv
        tvForgot.setOnClickListener(this)

        button = layout.loginbutton
        button.setOnClickListener(this)

        tvRegistrati = layout.registerTv
        tvRegistrati.setOnClickListener(this)

    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

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

        if (password.isEmpty()) {
            etPassword.error = "é richiesta una password"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "é richiesta una password di più di 6 caratteri"
            etPassword.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user!!.isEmailVerified) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(
                            this,
                            "Log in effettuato con successo!",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                    } else {
                        Toast.makeText(
                            this,
                            "Controlla la tua email per verificare il tuo account",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Errore nel log in",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.loginbutton -> {
                loginUser()
            }

            R.id.registerTv -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

            R.id.forgotTv -> {
                startActivity(Intent(this, ForgotPswActivity::class.java))
            }
        }
    }


}