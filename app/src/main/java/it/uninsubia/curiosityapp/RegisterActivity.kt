package it.uninsubia.curiosityapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import it.uninsubia.curiosityapp.databinding.ActivityRegisterBinding

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
                                showToast("Utente registrato con successo!")
                                progressBar.visibility = View.VISIBLE
                                Thread.sleep(1000)
                                startActivity(Intent(this, LoginActivity::class.java))
                            } else {
                                showToast("Errore nel registrare l'utente! Riprova!")
                            }
                        }
                } else {
                    showToast("Errore nel registrare l'utente!")
                }
            }
    }

    private fun showToast(message: String) {
        val inflater: LayoutInflater = layoutInflater
        val layoutToast = inflater.inflate(R.layout.custom_snackbar, (findViewById(R.id.toast_root)))
        val toastText = layoutToast.findViewById<TextView>(R.id.tv_snackBar)
        toastText.text = message
        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layoutToast
        toast.show()
    }
}