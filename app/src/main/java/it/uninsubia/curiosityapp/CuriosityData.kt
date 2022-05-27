package it.uninsubia.curiosityapp

data class CuriosityData(val title: String, val text: String, val topic : String) {
    constructor() : this("", "", "")
}