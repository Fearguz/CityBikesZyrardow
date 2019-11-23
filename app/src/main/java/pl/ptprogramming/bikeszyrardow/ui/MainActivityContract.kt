package pl.ptprogramming.bikeszyrardow.ui

import java.util.concurrent.TimeUnit
import org.osmdroid.util.GeoPoint
import pl.ptprogramming.bikeszyrardow.api.NetworkId
import pl.ptprogramming.bikeszyrardow.model.Station

interface MainActivityContract {
    interface View {
        fun updateMap(centerPoint: GeoPoint, stations: List<Station>)
        fun updateStations(stations: List<Station>)
        fun showError()
    }
    interface Presenter : BasePresenter<View> {
        fun loadNetwork(networkId: NetworkId)
        fun scheduleStationsUpdate(networkId: NetworkId, interval: Long, unit: TimeUnit)
        fun stopStationsUpdate()
    }
}