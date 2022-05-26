package it.uninsubia.curiosityapp

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CuriositiesRepository(
    private val field : String,
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("curiosità"),
    private val curiositiesRef: DatabaseReference = rootRef.child(field)
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