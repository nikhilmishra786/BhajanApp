package com.infomantri.bhakti.bhajan.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.infomantri.bhakti.bhajan.data.model.youtuberesponse.YoutubeApiResponse
import com.infomantri.bhakti.bhajan.network.doYoutubeApiResponse

class YoutubeViewModel : ViewModel() {

    var mutableYoutubeApiResponse: MutableLiveData<YoutubeApiResponse> = MutableLiveData()

    fun getYoutubeApiResponse(): LiveData<YoutubeApiResponse?> = mutableYoutubeApiResponse

    fun setYoutubeApiResponse(youtubeApiResponse: YoutubeApiResponse?) {
        mutableYoutubeApiResponse.postValue(youtubeApiResponse)
    }

    fun doYoutubeApiCall(videoId: String) {
        doYoutubeApiResponse(videoId)
    }

}