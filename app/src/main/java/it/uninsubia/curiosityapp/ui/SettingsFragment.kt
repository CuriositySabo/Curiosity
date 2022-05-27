package it.uninsubia.curiosityapp.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import it.uninsubia.curiosityapp.PostNotificationReceiver
import it.uninsubia.curiosityapp.R
import it.uninsubia.curiosityapp.SettingsData

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            if(key.equals("notification"))
            {
                val booleano = sharedPreferences.getBoolean("notification",false)
                Log.e("flags",booleano.toString())
                if(booleano)
                    notificationLauncher()
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun notificationLauncher() {
        var time = 1
        time *= 2000 // in realtà ce ne mette di più

        SettingsData(time)

        Toast.makeText(context, "Notifica Settata", Toast.LENGTH_LONG).show()

        //creazione intent con il broadcast da inviare
        val intent = Intent(context, PostNotificationReceiver::class.java)


        val pendingIntent =
            PendingIntent.getBroadcast(context, context.hashCode(), intent, PendingIntent.FLAG_MUTABLE)

        val momentTime = System.currentTimeMillis() // per salvare l'orario in quel dato momento


        val alarmManager =
            context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento

        /*
         setto una sveglia, secondo il tempo prestabilito, che viene lanciate anche a se il
         device è in sleep. Quando la sveglia "suona" viene lanciato il pending intent ovvero il
         in broadcast
         */
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            momentTime + time,
            pendingIntent
        )
    }

}