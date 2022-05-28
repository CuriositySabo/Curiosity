package it.uninsubia.curiosityapp

data class KnownCuriositiesData(var knowncuriosities: HashMap<Int, Boolean>) {
    constructor() : this (hashMapOf<Int, Boolean>())

    fun getSize(): Int
    {
        return knowncuriosities.size
    }
}

//map <code, boolean>

//var code = "$title $text topic".hashCode()