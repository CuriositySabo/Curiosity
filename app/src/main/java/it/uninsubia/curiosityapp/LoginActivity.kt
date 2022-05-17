package it.uninsubia.curiosityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding
import it.uninsubia.curiosityapp.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var layout: ActivityLoginBinding

    private lateinit var button: Button
    private lateinit var tvRegistrati: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        if(auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)



        layout = ActivityLoginBinding.inflate(layoutInflater)
        etEmail = layout.editTextEmail
        etPassword = layout.editTextPassword
        progressBar = layout.progressbar

        button = layout.loginbutton
        button.setOnClickListener(this)

        tvRegistrati = layout.registerTv
        tvRegistrati.setOnClickListener(this)


        setContentView(layout.root)
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
            .addOnCompleteListener() {
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
                    } else {
                        Toast.makeText(
                            this,
                            "Controlla la tua email per verificare il tuo account",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Errore nel log in",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.loginbutton -> {
                loginUser()
            }

            R.id.registerTv -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }


}