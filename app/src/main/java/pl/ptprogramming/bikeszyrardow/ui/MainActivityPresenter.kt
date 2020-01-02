package pl.ptprogramming.bikeszyrardow.ui

import java.lang.Exception
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import pl.ptprogramming.bikeszyrardow.api.BikesServiceAPI
import pl.ptprogramming.bikeszyrardow.api.NetworkId
import pl.ptprogramming.bikeszyrardow.model.Network
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(private val bikesApi: BikesServiceAPI) : MainActivityContract.Presenter
{
    private lateinit var view: MainActivityContract.View
    private var stationsUpdater: Job? = null

    override fun attach(view: MainActivityContract.View) {
        this.view = view
    }

    override fun loadNetwork(networkId: NetworkId) {
        println("Loading city bikes network of $networkId...")

        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                bikesApi.loadNetwork(networkId.toString())
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callFailed(e.message) }
                null
            }

            withContext(Dispatchers.Main) {
                processMapUpdate(response?.network)
            }
        }
    }

    private fun processMapUpdate(network: Network?) = if (network != null) {
        try {
            view.updateMap(GeoPoint(network.location!!.latitude, network.location!!.longitude), network.stations)
        } catch (e: NullPointerException) {
            callFailed("Response is incomplete.")
        }
    } else callFailed("Response is not available.")


    override fun scheduleStationsUpdate(networkId: NetworkId, interval: Long, unit: TimeUnit) {
        stationsUpdater = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                println("Loading city bikes stations of $networkId...")
                val response = try {
                    bikesApi.loadStations(networkId.toString())
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { callFailed(e.message) }
                    null
                }

                withContext(Dispatchers.Main) {
                    processStationsUpdate(response?.network)
                }
                delay(unit.toMillis(interval))
            }
        }
    }

    private fun processStationsUpdate(network: Network?) = network?.let {
        view.updateStations(network.stations)
    }

    private fun callFailed(message: String?) {
        println("Call to the City Bikes API failed: $message")
        view.showError()
    }

    override fun stopStationsUpdate() {
        runBlocking {
            stationsUpdater?.cancelAndJoin()
            stationsUpdater = null
        }
    }
}