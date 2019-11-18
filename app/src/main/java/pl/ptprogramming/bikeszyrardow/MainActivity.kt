package pl.ptprogramming.bikeszyrardow

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), MainActivityContract.View {

    private val TAG = "MainActivity"

    private val permissionsRequestCode = 101
    private val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val presenter by lazy { MainActivityPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()
        configureMap()
        presenter.view = this
    }

    override fun onStart() {
        super.onStart()

        presenter.loadNetwork(NetworkId.Zyrardow)
    }

    override fun onResume() {
        super.onResume()

        map.onResume()
        presenter.scheduleStationsUpdate(NetworkId.Zyrardow, resources.getInteger(R.integer.stations_update_interval_seconds).toLong(), TimeUnit.SECONDS)
    }

    override fun onPause() {
        super.onPause()

        map.onPause()
        presenter.stopStationsUpdate()
    }

    override fun updateMap(centerPoint: GeoPoint, stations: List<Station>) {
        Log.d(TAG, "Map update...")
        val markers = stations.map {
            createMarker(it)
        }
        map.overlays.addAll(markers)
        map.invalidate()

        with (map.controller) {
            setZoom(14.5)
            animateTo(centerPoint)
        }
    }

    override fun updateStations(stations: List<Station>) {
        Log.d(TAG, "Stations update...")
        val markers = stations.map {
            createMarker(it)
        }
        for (marker in markers) {
            if (marker in map.overlays) {
                map.overlays.remove(marker)
                map.overlays.add(marker)
            }
        }
        map.invalidate()
    }

    override fun showError() = Toast.makeText(this, resources.getText(R.string.network_error), Toast.LENGTH_LONG).show()

    private fun configureMap() {
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setMultiTouchControls(true)

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), permissionsRequestCode)
    }

    private fun createMarker(station: Station) = Marker(map).apply {
            title = station.name
            position = GeoPoint(station.latitude, station.longitude)
            icon = resources.getDrawable(R.drawable.ic_location, null)
            subDescription = resources.getString(R.string.station_description, station.free_bikes, station.empty_slots)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
}
