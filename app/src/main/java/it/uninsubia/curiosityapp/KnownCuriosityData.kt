package it.uninsubia.curiosityapp

data class KnownCuriosityData(var knowncuriosity: HashMap<String, HashMap<Long, Boolean>>) {
    constructor() : this (hashMapOf<String, HashMap<Long, Boolean>>())
}

//map <topic, map <hashcode, boolean>

//var code = "$title $text".hashCode().toLong()