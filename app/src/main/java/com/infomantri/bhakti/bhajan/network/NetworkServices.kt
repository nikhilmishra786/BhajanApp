package com.syngenta.pack.network

import com.infomantri.bhakti.bhajan.data.model.youtuberesponse.YoutubeApiResponse
import retrofit2.Call
import retrofit2.http.*

interface NetworkServices {

    @GET
    fun getYoutubeAPiResponse(
        @Url path: String
    ): Call<YoutubeApiResponse>

}