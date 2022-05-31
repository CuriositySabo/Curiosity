package it.uninsubia.curiosityapp

// ogni curiosità sul db è presente in questo formato
data class CuriosityData(val title: String, val text: String, val topic: String) {
    constructor() : this("", "", "")
}