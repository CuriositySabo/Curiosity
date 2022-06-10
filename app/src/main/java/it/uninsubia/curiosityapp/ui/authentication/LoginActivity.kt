package it.uninsubia.curiosityapp.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.R
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding
import it.uninsubia.curiosityapp.ui.MainActivity

// Activity realizzata per gestire la form di Login mostrata all'utente
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tvForgot: TextView
    private lateinit var layout: ActivityLoginBinding
    private lateinit var button: Button
    private lateinit var tvRegistrati: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var etEmail: com.google.android.material.textfield.TextInputEditText
    private lateinit var etPassword: com.google.android.material.textfield.TextInputEditText

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        // Se l'app è già stata utilizzata e l'accesso è già stato effettuato almeno un volta loggo automaticamente l'utente e non creo l'activity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            this.finish()
        }

        // Inizializzo componenti utili e setto alcuni Listener
        super.onCreate(savedInstanceState)
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

    // Checko i campi inseriti se rispettano gli standard (come password o email di un certo tipo)
    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

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
        if (password.isEmpty()) {
            etPassword.error = "É richiesta una password"
            etPassword.requestFocus()
            return
        }
        if (password.length < 6) {
            etPassword.error = "É richiesta una password di più di 6 caratteri"
            etPassword.requestFocus()
            return
        }

        // effettuo il log in e reindirizzo l'utente alla MainActivity se il login ha successo
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user!!.isEmailVerified) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        Toast.makeText(
                            applicationContext,
                            "Login effettuato con successo!",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Controlla la tua email per verificare il tuo account",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Email o password errate",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // metodo che effettua un operazione a seconda del bottone cliccato
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