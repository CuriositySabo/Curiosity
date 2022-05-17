package it.uninsubia.curiosityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var layout: ActivityRegisterBinding

    private lateinit var etnome: EditText
    private lateinit var etcognome: EditText
    private lateinit var etemail: EditText
    private lateinit var etpassword: EditText
    private lateinit var button: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    private val dbUrl = "https://curiosity-db178-default-rtdb.firebaseio.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        auth = Firebase.auth

        layout = ActivityRegisterBinding.inflate(layoutInflater)
        etnome = layout.editTextName
        etcognome = layout.editTextCognome
        etemail = layout.editTextEmail
        etpassword = layout.editTextPassword
        button = layout.registerbutton
        progressBar = layout.progressbar

        button.setOnClickListener {
            registerUser()
        }

        setContentView(layout.root)
    }

    private fun registerUser() {
        val nome = etnome.text.toString().trim()
        val cognome = etcognome.text.toString().trim()
        val email = etemail.text.toString().trim()
        val password = etpassword.text.toString().trim()


        if (nome.isEmpty()) {
            etnome.error = "é richiesto il tuo nome"
            etnome.requestFocus()
            return
        }

        if (cognome.isEmpty()) {
            etcognome.error = "é richiesto il tuo cognome"
            etcognome.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etemail.error = "é richiesta una email"
            etemail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.error = "É necessario inserire una email esistente"
            etemail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etpassword.error = "é richiesta una password"
            etpassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etpassword.error = "é richiesta una password di più di 6 caratteri"
            etpassword.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userFirebase = FirebaseAuth.getInstance().currentUser
                    userFirebase!!.sendEmailVerification()
                    val user = User(nome, cognome, email)
                    FirebaseDatabase.getInstance(dbUrl).getReference("users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Utente registrato correttamente!",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.visibility = View.GONE
                            } else {
                                Toast.makeText(
                                    this,
                                    "Errore nel registrare l'utente! Riprova!",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.visibility = View.GONE
                            }
                        }

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Errore nel registrare l'utente!", Toast.LENGTH_LONG)
                        .show()
                    progressBar.visibility = View.GONE
                }

                progressBar.visibility = View.GONE
            }


    }
}