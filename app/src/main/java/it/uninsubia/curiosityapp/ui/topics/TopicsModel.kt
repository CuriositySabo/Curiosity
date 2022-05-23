package it.uninsubia.curiosityapp.ui.topics

data class TopicsModel(var topicName: String, var image: String) {
    override fun toString(): String {
        return "Topics(name='$topicName',image='$image')"
    }
}