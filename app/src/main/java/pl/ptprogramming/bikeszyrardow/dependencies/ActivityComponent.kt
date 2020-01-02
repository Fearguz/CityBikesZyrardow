package pl.ptprogramming.bikeszyrardow.dependencies

import dagger.Component
import pl.ptprogramming.bikeszyrardow.ui.MainActivity

@Component(modules = [BikesApiModule::class, ActivityModule::class])
interface ActivityComponent {
    fun inject(activity: MainActivity)
}