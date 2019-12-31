package pl.ptprogramming.bikeszyrardow.ui

import java.util.concurrent.TimeUnit
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import javax.inject.Inject
import pl.ptprogramming.bikeszyrardow.BuildConfig
import pl.ptprogramming.bikeszyrardow.R
import pl.ptprogramming.bikeszyrardow.api.NetworkId
import pl.ptprogramming.bikeszyrardow.dependencies.ActivityModule
import pl.ptprogramming.bikeszyrardow.dependencies.DaggerActivityComponent
import pl.ptprogramming.bikeszyrardow.model.Station

class MainActivity : AppCompatActivity(), MainActivityContract.View {

    @Inject lateinit var presenter: MainActivityContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        injectDependencies()
        presenter.attach(this)

        requestPermissions()
        configureMap()
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
        stations
            .map { createMarker(it) }
            .let {
                map.overlays.addAll(it)
                map.invalidate()
            }
        with (map.controller) {
            setZoom(14.5)
            animateTo(centerPoint)
        }
    }

    override fun updateStations(stations: List<Station>) {
        stations
            .map { createMarker(it) }
            .forEach { marker ->
                if (marker in map.overlays) {
                    map.overlays.remove(marker)
                }
                map.overlays.add(marker)
            }
        map.invalidate()
    }

    override fun showError() = Toast
        .makeText(this, resources.getText(R.string.network_error), Toast.LENGTH_LONG)
        .show()

    private fun injectDependencies() {
        DaggerActivityComponent.builder()
            .activityModule(ActivityModule())
            .build()
            .inject(this)
    }

    private fun configureMap() {
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setMultiTouchControls(true)

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun requestPermissions() {
        val requestCode = 101
        val permissions = arrayOf(
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun createMarker(station: Station) = Marker(map).apply {
            title = station.name
            position = GeoPoint(station.latitude, station.longitude)
            icon = resources.getDrawable(R.drawable.ic_location, null)
            subDescription = resources.getString(R.string.station_description, station.free_bikes, station.empty_slots)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
}
