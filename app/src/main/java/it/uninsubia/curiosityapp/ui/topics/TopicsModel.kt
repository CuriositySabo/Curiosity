package it.uninsubia.curiosityapp.ui.topics
//contiene i dati di ogni topic
data class TopicsModel(var topicName: String, var image: String, var checked: Boolean) {
    override fun toString(): String {
        return "Topics(name='$topicName',image='$image',checked='$checked')"
    }
}