package it.uninsubia.curiosityapp

data class NotificationData(var title: String, var text: String, var topic: String) {
    constructor() : this("", "", "")
}