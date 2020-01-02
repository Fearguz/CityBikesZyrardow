package pl.ptprogramming.bikeszyrardow.dependencies

import dagger.Module
import dagger.Provides
import pl.ptprogramming.bikeszyrardow.api.BikesServiceAPI

@Module
class BikesApiModule {
    @Provides
    fun provideBikesApi(): BikesServiceAPI = BikesServiceAPI.create()
}