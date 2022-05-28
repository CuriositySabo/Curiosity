package it.uninsubia.curiosityapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import it.uninsubia.curiosityapp.R
import it.uninsubia.curiosityapp.SettingsData
import it.uninsubia.curiosityapp.Utility
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        registerSettingsListener()
    }



    private fun writeFile(time : Int) {

        val directory = File("${context?.filesDir}/tmp") // path della directory
        val filepath = File("$directory/settings.json") // path del filepath

        try {
            PrintWriter(FileWriter(filepath)).use {
                val gson = Gson()
                val jsonString = gson.toJson(SettingsData(time))
                // scrive la classe contenente i settings in formato json sul file
                it.write(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerSettingsListener() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if(key.equals("notification"))
            {
                val booleano = sharedPreferences.getBoolean("notification",false)
                Log.e("flags",booleano.toString())
                if(booleano)
                    Utility.notificationLauncher(requireContext())
            }

            if(key.equals("frequency")) {
                val frequency = sharedPreferences.getString("frequency", "30")!!.toInt()
                writeFile(frequency)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

}