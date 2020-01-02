package pl.ptprogramming.bikeszyrardow

import dagger.Binds
import dagger.Module
import dagger.Provides
import pl.ptprogramming.bikeszyrardow.api.BikesServiceAPI
import pl.ptprogramming.bikeszyrardow.ui.MainActivityContract
import pl.ptprogramming.bikeszyrardow.ui.MainActivityPresenter

@Module
class TestModule {
    @Provides
    fun provideBikesApi(): BikesServiceAPI = BikesServiceMock()
}

@Module
abstract class TestModuleBinder {
    @Binds
    abstract fun bindPresenter(presenter: MainActivityPresenter): MainActivityContract.Presenter
}