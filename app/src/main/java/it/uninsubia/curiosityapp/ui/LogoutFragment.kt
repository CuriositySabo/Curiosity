package it.uninsubia.curiosityapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.FragmentLogoutBinding
import it.uninsubia.curiosityapp.ui.authentication.LoginActivity

class LogoutFragment : Fragment() {

    private var _binding: FragmentLogoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val auth = FirebaseAuth.getInstance()
        return root
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        this.onDestroyView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}