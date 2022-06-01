package it.uninsubia.curiosityapp.database

// interfaccia per implementare un modo per notificare al sistema se ha finito le operazioni download dal db
interface FirebaseCallback {
    fun onResponse(response: Response)
}