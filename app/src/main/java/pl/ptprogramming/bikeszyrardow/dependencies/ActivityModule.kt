package pl.ptprogramming.bikeszyrardow.dependencies

import dagger.Module
import dagger.Provides
import pl.ptprogramming.bikeszyrardow.ui.MainActivityContract
import pl.ptprogramming.bikeszyrardow.ui.MainActivityPresenter

@Module
class ActivityModule {
    @Provides
    fun providePresenter(): MainActivityContract.Presenter = MainActivityPresenter()
}