package pl.ptprogramming.bikeszyrardow

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val CityBikeBaseUrl = "https://api.citybik.es"

enum class NetworkId(val id: String) {
    Zyrardow("zyrardowski-rower-miejski-zyrardow")
}

interface BikesAPI
{
    @GET("/v2/networks/{id}")
    fun loadNetwork(@Path("id") networkId: String): Call<NetworkResponse>

    @GET("/v2/networks/{id}")
    fun loadNetworkStations(@Path("id") networkId: String, @Query("fields") fields: List<String> = listOf("stations")): Call<NetworkResponse>
}