package pl.ptprogramming.bikeszyrardow

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.osmdroid.util.GeoPoint
import pl.ptprogramming.bikeszyrardow.api.NetworkId
import pl.ptprogramming.bikeszyrardow.ui.MainActivityContract
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainPresenterTest
{
    @Inject
    lateinit var presenter: MainActivityContract.Presenter

    private val activity: MainActivityContract.View = Mockito.mock(MainActivityContract.View::class.java)

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @Before
    fun setUp() {
        DaggerTestComponent.builder().testModule(TestModule()).build().inject(this)
        presenter.attach(activity)

        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `givenServiceMock_whenLoadingNetwork_thenVerify`() {
        runBlocking {
            println("Coroutine START")
            presenter.loadNetwork(NetworkId.Zyrardow)
            println("Coroutine END")
        }

        println("TEST")
        verify(activity)
            .updateMap(GeoPoint(BikesServiceMock.location.latitude, BikesServiceMock.location.longitude),
                BikesServiceMock.stations)
        println("TEST END")
    }

    @Test
    fun `givenServiceMock_whenScheduleLoadingStations_thenVerifyIfStationsLoaded`() {
        runBlocking {
            presenter.scheduleStationsUpdate(NetworkId.Zyrardow, 100, TimeUnit.MILLISECONDS)
            delay(150)

            presenter.stopStationsUpdate()
            delay(100)
        }
        verify(activity, times(2))
            .updateStations(BikesServiceMock.stations)
    }

    @Test
    fun `givenServiceMock_whenScheduleLoadingStationsTwice_thenVerifyIfStationsLoadingRetried`() {
        runBlocking {
            presenter.scheduleStationsUpdate(NetworkId.Zyrardow, 100, TimeUnit.MILLISECONDS)
            delay(150)

            presenter.stopStationsUpdate()
            delay(100)

            presenter.scheduleStationsUpdate(NetworkId.Zyrardow, 100, TimeUnit.MILLISECONDS)
            delay(150)

            presenter.stopStationsUpdate()
        }
        verify(activity, times(4))
            .updateStations(BikesServiceMock.stations)
    }
}