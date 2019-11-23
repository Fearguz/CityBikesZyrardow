package pl.ptprogramming.bikeszyrardow.model

data class NetworkResponse(var network: Network?)

data class Network(var name: String,
                   var location: NetworkLocation?,
                   var stations: List<Station>)

data class NetworkLocation(var city: String,
                           var country: String,
                           var latitude: Double,
                           var longitude: Double)

data class Station(var name: String,
                   var latitude: Double,
                   var longitude: Double,
                   var empty_slots: Int,
                   var free_bikes: Int)