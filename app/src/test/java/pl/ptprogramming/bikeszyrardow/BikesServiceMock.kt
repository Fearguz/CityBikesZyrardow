package pl.ptprogramming.bikeszyrardow

import pl.ptprogramming.bikeszyrardow.api.BikesServiceAPI
import pl.ptprogramming.bikeszyrardow.model.Network
import pl.ptprogramming.bikeszyrardow.model.NetworkLocation
import pl.ptprogramming.bikeszyrardow.model.NetworkResponse
import pl.ptprogramming.bikeszyrardow.model.Station

class BikesServiceMock : BikesServiceAPI
{
    companion object {
        const val networkName = "Zyrardow"
        val stations = listOf(Station("Station", 0.0, 0.0, 0, 0))
        val location = NetworkLocation(networkName, "PL", 0.0, 0.0)
    }

    override suspend fun loadNetwork(networkId: String): NetworkResponse = NetworkResponse(Network(networkName, location, stations))

    override suspend fun loadStations(networkId: String): NetworkResponse = NetworkResponse(Network(networkName, null, stations))
}