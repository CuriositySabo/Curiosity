package it.uninsubia.curiosityapp.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import it.uninsubia.curiosityapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }




}