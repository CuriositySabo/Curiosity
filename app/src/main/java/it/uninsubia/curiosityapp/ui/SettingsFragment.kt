package it.uninsubia.curiosityapp.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.gson.Gson
import it.uninsubia.curiosityapp.notification.KnownCuriositiesData
import it.uninsubia.curiosityapp.R
import it.uninsubia.curiosityapp.Utility
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class SettingsFragment : PreferenceFragmentCompat() {
    //le preferences sono le impostazioni dell'applicazione.
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //serve per resettare il file contenente le curiosità sapute e non sapute
        val resetPreference: Preference? = findPreference("resetBtn") as Preference?
        resetPreference!!.setOnPreferenceClickListener() {
            resetReceivedCuriosities(requireContext())
            Toast.makeText(
                requireContext(),
                "Ho resettato le curiosità ricevute",
                Toast.LENGTH_LONG
            ).show()
            true
        }
    }
    //funzione che resetta il file
    private fun resetReceivedCuriosities(context: Context) {
        val map = KnownCuriositiesData()
        val topics = Utility.initTopicList()
        for (topic in topics) {
            map.knowncuriosities[topic.topicName] = hashMapOf<Int, Boolean>()
        }
        val directory = File(context.filesDir, "tmp") // path directory tmp
        val filepath = File(directory, "knowncuriosities.json") // path del file

        PrintWriter(FileWriter(filepath)).use {
            val gson = Gson()
            // inizializzo con la classe vuota il file
            val jsonString = gson.toJson(map)
            it.write(jsonString)
        }
    }

}