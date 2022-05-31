package it.uninsubia.curiosityapp.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityRegisterBinding

// Activity realizzata per gestire la form di registrazione mostrata all'utente
class RegisterActivity : AppCompatActivity() {
    private lateinit var layout: ActivityRegisterBinding

    private lateinit var etnome: com.google.android.material.textfield.TextInputEditText
    private lateinit var etcognome: com.google.android.material.textfield.TextInputEditText
    private lateinit var etemail: com.google.android.material.textfield.TextInputEditText
    private lateinit var etpassword: com.google.android.material.textfield.TextInputEditText
    private lateinit var button: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLogin: TextView

    private lateinit var auth: FirebaseAuth

    private val dbUrl = "https://curiosity-db178-default-rtdb.firebaseio.com"

    // Inizializzo componenti utili e setto alcuni Listener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(layout.root)

        auth = Firebase.auth

        etnome = layout.editTextName
        etcognome = layout.editTextCognome
        etemail = layout.editTextEmail
        etpassword = layout.editTextPassword
        button = layout.registerbutton
        progressBar = layout.progressbar
        tvLogin = layout.loginTv

        button.setOnClickListener {
            registerUser()
        }
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Checko i campi inseriti dall'utente se sono tutti ok registro l'utente sul db
    private fun registerUser() {
        val nome = etnome.text.toString().trim()
        val cognome = etcognome.text.toString().trim()
        val email = etemail.text.toString().trim()
        val password = etpassword.text.toString().trim()

        if (nome.isEmpty()) {
            etnome.error = "É richiesto il tuo nome"
            etnome.requestFocus()
            return
        }
        if (cognome.isEmpty()) {
            etcognome.error = "É richiesto il tuo cognome"
            etcognome.requestFocus()
            return
        }
        if (email.isEmpty()) {
            etemail.error = "É richiesta la tua email"
            etemail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.error = "Inserisci una email esistente!"
            etemail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etpassword.error = "É richiesta la tua password"
            etpassword.requestFocus()
            return
        }
        if (password.length < 6) {
            etpassword.error = "É richiesta una password di almeno 6 caratteri"
            etpassword.requestFocus()
            return
        }

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
                                progressBar.visibility = View.VISIBLE
                                startActivity(Intent(this, LoginActivity::class.java))
                                Toast.makeText(
                                    applicationContext,
                                    "Utente registrato con successo!",
                                    Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Errore nel registrare l'utente! Riprova!",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Errore nel registrare l'utente!",
                        Toast.LENGTH_LONG).show()
                }
            }
    }
}