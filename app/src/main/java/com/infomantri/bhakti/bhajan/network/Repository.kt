package com.infomantri.bhakti.bhajan.network

import android.app.Activity
import android.util.Log
import com.infomantri.bhakti.bhajan.data.model.youtuberesponse.YoutubeApiResponse
import com.infomantri.bhakti.bhajan.data.viewmodel.YoutubeViewModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

fun YoutubeViewModel.doYoutubeApiResponse(videoId: String) {

    RetrofitClient.getService()
        ?.getYoutubeAPiResponse("videos?part=id%2C+snippet&id=lSGl0WUKEdU&key=AIzaSyA9y2AKALhQS8e7BlY1HFk72flEh60HDD8")
        ?.enqueue(
            object : Callback<YoutubeApiResponse> {
                override fun onResponse(
                    call: Call<YoutubeApiResponse>?,
                    response: Response<YoutubeApiResponse>?
                ) {
                    setYoutubeApiResponse(response?.body())
                    Log.v("Youtube_Api_Response", ">>> response.body ${response?.body()}")
                }

                override fun onFailure(call: Call<YoutubeApiResponse>, t: Throwable) {
                    setYoutubeApiResponse(null)
                    Log.v("Youtube_Api_Response", ">>> response.error ${t.message}")
                }
            }
        )
}