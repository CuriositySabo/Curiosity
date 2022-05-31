package it.uninsubia.curiosityapp.notification

// Formato dei dati utilizzatto per le operazioni di I/O sul file
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

