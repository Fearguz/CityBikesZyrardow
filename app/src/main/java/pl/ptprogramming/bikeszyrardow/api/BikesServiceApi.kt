package pl.ptprogramming.bikeszyrardow.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

import pl.ptprogramming.bikeszyrardow.model.NetworkResponse

const val CityBikeBaseUrl = "https://api.citybik.es"

enum class NetworkId(private val id: String) {
    Zyrardow("zyrardowski-rower-miejski-zyrardow");

    override fun toString(): String = id
}

interface BikesServiceAPI
{
    @GET("/v2/networks/{id}")
    suspend fun loadNetwork(@Path("id") networkId: String): NetworkResponse

    @GET("/v2/networks/{id}?fields=stations")
    suspend fun loadStations(@Path("id") networkId: String): NetworkResponse

    companion object Factory {
        fun create(): BikesServiceAPI = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CityBikeBaseUrl)
                .build()
                .create(BikesServiceAPI::class.java)
    }
}