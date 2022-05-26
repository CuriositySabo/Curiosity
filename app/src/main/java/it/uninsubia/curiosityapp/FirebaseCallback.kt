package it.uninsubia.myfirebasetest

import it.uninsubia.curiosityapp.Response

interface FirebaseCallback {
    fun onResponse(response: Response)
}