package it.uninsubia.curiosityapp.database

// Tipo di dati contenuti nella repository di curiosità locale
data class Response(
    var curiosities: List<CuriosityData>? = null,
    var exception: Exception? = null
)