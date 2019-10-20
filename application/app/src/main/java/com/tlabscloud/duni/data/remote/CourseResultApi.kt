package com.tlabscloud.duni.data.remote


import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

interface CourseResultApi {
  /*  @GET("/vehicle")
    suspend fun getVehicles(
        @Query("bottomLeft") bottomLeft: String,
        @Query("upperRight") upperRight: String
    ): Response<List<VehicleDto>>

    @POST("/vehicle/{address}/book")
    suspend fun bookVehicle(
        @Path("address") address: String,
        @Body body: WalletDto
    ): ResponseBody

    @POST("/vehicle/{address}/reserve")
    suspend fun reserveVehicle(
        @Path("address") address: String
    ): Response<XrideResponseDto<ReservationDto>>

    @DELETE("/vehicle/{address}/reserve")
    suspend fun cancelReserveVehicle(
        @Path("address") address: String
    ): Response<Void>

    @POST("/vehicle/{address}/book/stop")
    suspend fun stopBookVehicle(
        @Path("address") address: String,
        @Body body: WalletDto
    ): ResponseBody

    @Multipart
    @POST("/vehicle")
    suspend fun addVehicle(
        @Part vehicle: MultipartBody.Part,
        @Part("wallet") wallet: RequestBody,
        @Part("walletPrivateKey") walletPrivateKey: RequestBody,
        @Part image: MultipartBody.Part
    ): ResponseBody*/
}

val courseResultApiModule = Kodein.Module("CourseResultAPI") {
    bind<CourseResultApi>() with singleton {
        val retrofit: Retrofit = instance("courseRetrofit")
        retrofit.create(CourseResultApi::class.java)
    }
}
