package it.uninsubia.curiosityapp

data class KnownCuriositiesData(var knowncuriosities: HashMap<String, HashMap<Int, Boolean>>) {

    constructor() : this(hashMapOf<String, HashMap<Int, Boolean>>())

    fun getSize(): Int {
        var count = 0
        knowncuriosities.forEach() {
            count += it.value.size
        }
        return count
    }
}

//map <code, boolean>

//var code = "$title $text topic".hashCode()