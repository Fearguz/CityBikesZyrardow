package pl.ptprogramming.bikeszyrardow

import android.util.Log
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.NullPointerException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MainActivityPresenter : BasePresenter<MainActivityContract.View>(), MainActivityContract.Presenter
{
    private val TAG = "MainActivityPresenter"

    private val bikesApi by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CityBikeBaseUrl)
            .build()
        retrofit.create(BikesAPI::class.java)
    }
    private var scheduledStationsUpdateExecutor: ScheduledFuture<*>? = null

    override fun loadNetwork(networkId: NetworkId) {
        Log.d(TAG, "Loading network...")

        val call = bikesApi.loadNetwork(networkId.id)
        call.enqueue(object : Callback<NetworkResponse> {
            override fun onFailure(call: Call<NetworkResponse>?, t: Throwable?) {
                callFailed(t?.message)
            }

            override fun onResponse(call: Call<NetworkResponse>?, response: Response<NetworkResponse>?) {
                response?.let {
                    if (it.isSuccessful) {
                        processMapUpdate(it.body().network)
                    } else {
                        callFailed(it.errorBody().string())
                    }
                }
            }
        })
    }

    override fun scheduleStationsUpdate(networkId: NetworkId, interval: Long, unit: TimeUnit) {
        val runnable = Runnable {
            Log.d(TAG, "Loading network stations...")

            val call = bikesApi.loadNetworkStations(networkId.id)
            call.enqueue(object : Callback<NetworkResponse> {
                override fun onFailure(call: Call<NetworkResponse>?, t: Throwable?) {
                    callFailed(t?.message)
                }

                override fun onResponse(call: Call<NetworkResponse>?, response: Response<NetworkResponse>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            processStationsUpdate(it.body().network)
                        } else {
                            callFailed(it.errorBody().string())
                        }
                    }
                }
            })
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
        view?.showError()
    }

    private fun processMapUpdate(network: Network?) = if (network != null) {
            try {
                view?.updateMap(GeoPoint(network.location!!.latitude, network.location!!.longitude), network.stations)
            } catch (e: NullPointerException) {
                callFailed("Response is incomplete.")
            }
        } else callFailed("Response is not available.")

    private fun processStationsUpdate(network: Network?) = network?.let {
        view?.updateStations(network.stations)
    }
}