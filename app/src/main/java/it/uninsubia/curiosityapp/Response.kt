package it.uninsubia.curiosityapp
// Tipo di dati contenuti nella repository di curiosità locale
data class Response(
    var curiosities: List<CuriosityData>? = null,
    var exception: Exception? = null
)