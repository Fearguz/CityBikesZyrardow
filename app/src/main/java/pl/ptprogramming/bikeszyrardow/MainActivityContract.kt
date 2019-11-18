package pl.ptprogramming.bikeszyrardow

import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit

interface MainActivityContract
{
    interface View {
        fun updateMap(centerPoint: GeoPoint, stations: List<Station>)
        fun updateStations(stations: List<Station>)
        fun showError()
    }

    interface Presenter {
        fun loadNetwork(networkId: NetworkId)
        fun scheduleStationsUpdate(networkId: NetworkId, interval: Long, unit: TimeUnit)
        fun stopStationsUpdate()
    }
}