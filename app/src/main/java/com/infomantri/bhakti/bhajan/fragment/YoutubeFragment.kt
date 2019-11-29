package com.infomantri.bhakti.bhajan.fragment

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.*
import com.infomantri.bhakti.bhajan.R
import kotlinx.android.synthetic.main.fragment_youtube.*
import androidx.fragment.app.FragmentActivity


class YoutubeFragment : Fragment() {

    private var myContext: FragmentActivity? = null
    private var YPlayer: YouTubePlayer? = null


    private lateinit var webView: WebView
    private val url = "https://www.youtube.com/watch?v=9Q1kbzcCX1M"

    companion object

    val YoutubeAPIKey = "AIzaSyA9y2AKALhQS8e7BlY1HFk72flEh60HDD8"
    //    private val path = "/storage/Internal storage/bhaag_milkha.mp4"
//    private val path = "android.resource://${activity!!.packageName}/"

    override fun onAttach(context: Context) {
        if (activity is FragmentActivity) {
            myContext = activity as FragmentActivity
        }
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_youtube, container, false)

        val youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()
        youTubePlayerFragment.initialize(
            YoutubeAPIKey,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {

                }

                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider?,
                    youTubePlayer: YouTubePlayer?,
                    wasRestored: Boolean
                ) {
                    if (!wasRestored) {
                        YPlayer = youTubePlayer;
//                        YPlayer?.setShowFullscreenButton(true)
                        YPlayer?.loadVideo("Y43EutnQKCQ");
                        YPlayer?.play()
                        YPlayer?.fullscreenControlFlags =
                            YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                        YPlayer?.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION)
                    }
                }

            })

        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit()
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        myContext?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)


        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        myContext?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


//        wvYoutube.webViewClient = object: WebViewClient() {
//            override fun shouldOverrideUrlLoading(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): Boolean {
//                view?.loadUrl(request?.url.toString())
//                return true
//            }
//        }
//        wvYoutube.loadUrl(url)

//        Log.v("STORAGE_PATH", ">>> " + Uri.parse(path).toString())
//        Log.v("PATH", ">>> $path")
        Log.v("External_Storage_Abs", Environment.getDataDirectory().absolutePath)
        Log.v("External_Storage_Path", Environment.getDataDirectory().path)

        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        myContext?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }
}