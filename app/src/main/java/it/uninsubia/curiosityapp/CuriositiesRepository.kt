package it.uninsubia.myfirebasetest

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.uninsubia.curiosityapp.CuriosityData
import it.uninsubia.curiosityapp.Response

class CuriositiesRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val curiositiesRef: DatabaseReference = rootRef.child("curiosities")
) {
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