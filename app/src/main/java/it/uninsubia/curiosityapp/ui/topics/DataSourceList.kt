package it.uninsubia.curiosityapp.ui.topics

class DataSourceList {
    companion object {
        fun createData(): ArrayList<TopicsModel> {
            val list = ArrayList<TopicsModel>()
            list.add(
                TopicsModel(
                    "Cinema",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cinema.jpg"
                )
            )
            list.add(
                TopicsModel(
                    "Cucina",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cucina.jpg"
                )
            )
            list.add(
                TopicsModel(
                    "Storia",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/storia2.jpg"
                )
            )
            list.add(
                TopicsModel(
                    "Tecnologia",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/tecnologia.jpg"
                )
            )
            list.add(
                TopicsModel(
                    "Application",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/sport.jpg"
                )
            )
            return list
        }
    }
}