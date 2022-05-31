package it.uninsubia.curiosityapp
//contiene la risposta del database
data class Response(
    var curiosities: List<CuriosityData>? = null,
    var exception: Exception? = null
)