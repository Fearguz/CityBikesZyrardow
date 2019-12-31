package pl.ptprogramming.bikeszyrardow.ui

import java.lang.Exception
import java.lang.NullPointerException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import android.util.Log
import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import pl.ptprogramming.bikeszyrardow.api.BikesServiceAPI
import pl.ptprogramming.bikeszyrardow.api.NetworkId
import pl.ptprogramming.bikeszyrardow.model.Network
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(private val bikesApi: BikesServiceAPI) : MainActivityContract.Presenter
{
    private val TAG = "MainActivityPresenter"

    private lateinit var view: MainActivityContract.View
    private var scheduledStationsUpdateExecutor: ScheduledFuture<*>? = null

    override fun attach(view: MainActivityContract.View) {
        this.view = view
    }

    override fun loadNetwork(networkId: NetworkId) {
        Log.d(TAG, "Loading city bikes network of $networkId...")

        GlobalScope.launch {
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

    override fun scheduleStationsUpdate(networkId: NetworkId, interval: Long, unit: TimeUnit) {
        val runnable = Runnable {
            Log.d(TAG, "Loading city bikes stations of $networkId...")
            runBlocking {
                val response = try {
                    bikesApi.loadStations(networkId.toString())
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { callFailed(e.message) }
                    null
                }

                withContext(Dispatchers.Main) {
                    processStationsUpdate(response?.network)
                }
            }
        }
        scheduledStationsUpdateExecutor = Executors
            .newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(runnable, interval, interval, unit)
    }

    override fun stopStationsUpdate() {
        scheduledStationsUpdateExecutor?.cancel(false)
        scheduledStationsUpdateExecutor = null
    }

    private fun callFailed(message: String?) {
        Log.e(TAG, "Call to the City Bikes API failed: $message")
        view.showError()
    }

    private fun processMapUpdate(network: Network?) = if (network != null) {
            try {
                view.updateMap(GeoPoint(network.location!!.latitude, network.location!!.longitude), network.stations)
            } catch (e: NullPointerException) {
                callFailed("Response is incomplete.")
            }
        } else callFailed("Response is not available.")

    private fun processStationsUpdate(network: Network?) = network?.let {
        view.updateStations(network.stations)
    }
}