package it.uninsubia.curiosityapp

data class KnownCuriositiesData(var knowncuriosities: HashMap<String, HashMap<Int, Boolean>>) {
    constructor() : this (hashMapOf<String, HashMap<Int, Boolean>>())
}

//map <code, boolean>

//var code = "$title $text topic".hashCode()