package it.uninsubia.curiosityapp

data class Response(
    var curiosities: List<CuriosityData>? = null,
    var exception: Exception? = null
)