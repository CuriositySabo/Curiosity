package it.uninsubia.curiosityapp.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// salva in locale il contenuto del nodo curiosità presente sul realtime db di firebase
class CuriositiesRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val curiositiesRef: DatabaseReference = rootRef.child("curiosities")
) {
    //utilizza un meccanismo di callback per fare in modo che la classe che usa il metodo sappia quando è possibile usare la repository (ha scaricato tutti i dati)
    fun getResponseFromRealtimeDatabaseUsingCallback(callback: FirebaseCallback) {
        curiositiesRef.get().addOnCompleteListener { task ->
            val response = Response()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.curiosities = result.children.map { snapShot ->
                        snapShot.getValue(CuriosityData::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            callback.onResponse(response)
        }
    }
}