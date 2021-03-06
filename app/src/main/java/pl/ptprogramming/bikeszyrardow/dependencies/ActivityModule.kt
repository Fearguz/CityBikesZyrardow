package pl.ptprogramming.bikeszyrardow.dependencies

import dagger.Binds
import dagger.Module
import pl.ptprogramming.bikeszyrardow.ui.MainActivityContract
import pl.ptprogramming.bikeszyrardow.ui.MainActivityPresenter

@Module
abstract class ActivityModule {
    @Binds
    abstract fun bindPresenter(presenter: MainActivityPresenter): MainActivityContract.Presenter
}