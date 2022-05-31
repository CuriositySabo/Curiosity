package it.uninsubia.curiosityapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import it.uninsubia.curiosityapp.Utility

// Riceve se è stato premuto il bottone lo sapevo sulla notifica
class PositiveAnswerReceiver : BroadcastReceiver() {
    private val tag = "Positive answer"

    // Ricevuto il broadcast, ovvero la notifica di un dato evento al sistema, l'applicazione si comporterà nel modo seguente:
    override fun onReceive(context: Context?, intent: Intent?) {
        rescheduleNotification(context!!, intent!!)
    }

    // Rischedulo la notifica secondo le impostazioni settate
    private fun rescheduleNotification(context: Context, intent: Intent) {
        // Prendo il testo della notifica che lancia il broadcast
        val notificationData = intent.getStringArrayListExtra("notificationData")!!
        Log.e(tag, notificationData.toString())

        // Scrive la notifica sul file specificando che l'utente la conosceva
        Utility.writeKnownCuriositiesFile(context, notificationData, true)

        // Cancella la notifica che lancia il broadcast
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(200)

        // Rischedula un altra notifica
        Utility.notificationLauncher(context)

    }

}