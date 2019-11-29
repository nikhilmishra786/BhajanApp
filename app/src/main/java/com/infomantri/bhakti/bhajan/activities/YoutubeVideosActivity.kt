package com.infomantri.bhakti.bhajan.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YouTubeUriExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.youtube.player.*
import com.google.firebase.database.*
import com.infomantri.bhakti.bhajan.AppConstant
import com.infomantri.bhakti.bhajan.BaseActivity
import com.infomantri.bhakti.bhajan.R
import com.infomantri.bhakti.bhajan.Utils.DownloadUtil
import com.infomantri.bhakti.bhajan.Utils.initViewModel
import com.infomantri.bhakti.bhajan.adapter.YoutubeVideosAdapter
import com.infomantri.bhakti.bhajan.data.model.YoutubeVideo
import com.infomantri.bhakti.bhajan.data.viewmodel.YoutubeViewModel
import com.infomantri.bhakti.bhajan.database.YoutubeRoomDatabase
import com.infomantri.bhakti.bhajan.database.YoutubeVideoDB
import com.infomantri.bhakti.bhajan.database.YoutubeVideoRepository
import kotlinx.android.synthetic.main.activity_youtube_videos.*
import okhttp3.*
import java.io.File
import java.io.IOException

class YoutubeVideosActivity : BaseActivity(), YouTubePlayer.OnInitializedListener {

    val YOUTUBE_API_GET_JSON_DATA =
        "https://www.googleapis.com/youtube/v3/videos?part=id%2C+snippet&id="
    private val RECOVERY_DIALOG_REQUEST = 1
    lateinit var youTubePlayerFragment: YouTubePlayerFragment
    private var youTubePlayer: YouTubePlayer? = null
    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mFirebaseReference: DatabaseReference
    var videoIdList = ArrayList<String>()
    lateinit var mYoutubeViewModel: YoutubeViewModel

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        errorReason: YouTubeInitializationResult?
    ) {
        if (errorReason?.isUserRecoverableError == true) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage =
                String.format(getString(R.string.error_player), errorReason.toString())
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        player: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            youTubePlayer = player
            player?.loadVideo(mCurrentVideo)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_videos)

        mYoutubeViewModel = initViewModel()
        youtubeVideosAPiObserver()
        mCurrentVideo = intent?.extras?.getString(AppConstant.YOUTUBE_VIDEO_ID) ?: mCurrentVideo

        youTubePlayerFragment =
            fragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerFragment
        youTubePlayerFragment.initialize(AppConstant.YOUTUBE_API_KEY, this)

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseReference = mFirebaseDatabase.getReference(AppConstant.YOUTUBE_VIDEO)
        mFirebaseDatabase.getReference(AppConstant.IS_DOWNLOAD_AVAILABLE)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.getValue(Boolean::class.java) == true) {
                        downloadYoutubeVideoData()
                    }
                    Log.v(
                        "IS_DOWNLOAD_AVAILABLE",
                        ">>> IS_DOWNLOAD_AVAILABLE : ${dataSnapshot.value}"
                    )
                }

            })
        setRecyclerView()
        saveToDatabse()

    }

    val videosList = ArrayList<YoutubeVideo>()
    private lateinit var adapter: YoutubeVideosAdapter
    var mPosition = 0
    var mCurrentVideo = "nfjibXuiy98"
    var mPreviousVideo: YoutubeVideo? = null

    fun setRecyclerView() {
        adapter = YoutubeVideosAdapter(this) { position ->
            mPreviousVideo?.let {
                videosList.add(mPreviousVideo!!)
                adapter.notifyDataSetChanged()
            }
            mPosition = position
            mPreviousVideo = videosList[position]
            mCurrentVideo = videosList[position].videoId
            youTubePlayer?.loadVideo(mCurrentVideo)
            videosList.removeAt(position)
            adapter.notifyDataSetChanged()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        rvYoutubeVideos.layoutManager = linearLayoutManager
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvYoutubeVideos.adapter = adapter

        adapter.submitList(videosList)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {

        if (youTubePlayer?.isPlaying == false)
            youTubePlayer?.play()

        super.onResume()

    }

    private fun downloadYoutubeVideoData() {
        mFirebaseReference.limitToLast(5)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.v("LimitTOLAST", ">>> LimitTOLAST last n : ${dataSnapshot.value}")
                    val bhajanlist = dataSnapshot.children
                    bhajanlist.forEach {
                        it?.let {
                            videoIdList.add(it.getValue(String::class.java)!!)
                        }
                    }
                    saveToDatabse()

                    mFirebaseDatabase.getReference(AppConstant.IS_DOWNLOAD_AVAILABLE)
                        .setValue(false)
                }

                override fun onCancelled(p0: DatabaseError) {
                }

            })
    }

    private fun saveToDatabse() {
        val client = OkHttpClient()
        videoIdList.add(mCurrentVideo)
        videoIdList.forEach {
            val request = Request.Builder()
                .url(YOUTUBE_API_GET_JSON_DATA + "&key=" + AppConstant.YOUTUBE_API_KEY)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.v(
                        "YOUTUBE_GET_JSON_DATA",
                        ">>> YOUTUBE_GET_JSON_DATA Response: ${response.body()} call: $call"
                    )
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.v(
                        "YOUTUBE_GET_JSON_DATA",
                        ">>> Error..... YOUTUBE_API_GET_JSON_DATA call: $call error: $e"
                    )
                }
            })

            mYoutubeViewModel.doYoutubeApiCall(mCurrentVideo)
        }


        videoIdList.forEach {

        }
        adapter.notifyDataSetChanged()
    }

    fun youtubeVideosAPiObserver() {
        mYoutubeViewModel.getYoutubeApiResponse().observe(this, Observer { youtubeApiResponse ->
            youtubeApiResponse?.let {
                Log.v("Youtube_Api_Response", ">>> Youtube_Api_Response : $youtubeApiResponse")
            }
            youtubeExecutor()
        })
    }

    fun youtubeExecutor() {
        val youtubeUrl = "https://www.youtube.com/watch?v=$mCurrentVideo"
        var downloadUrlLink = ""
        var title = ""
        val youtubeExtractor = @SuppressLint("StaticFieldLeak")
        object : YouTubeExtractor(this) {

            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                Log.v(
                    "META_DATA",
                    ">>> Video Meta Data Download Url: ${ytFiles?.get(22)?.url} ytFiles: ${ytFiles?.size()}"
                )
                ytFiles?.let {
                    downloadUrlLink = ytFiles.get(22).url
                }
                videoMeta?.let {
                    title = videoMeta.title
                }
                downloadVideo(
                    downloadUrlLink = downloadUrlLink,
                    fileName = title.plus(".${ytFiles?.get(22)?.format?.ext}")
                )
            }

            override fun onPreExecute() {
                super.onPreExecute()
                Log.v("META_DATA", ">>> Video Meta Data : PreExecuter")

            }
        }

        youtubeExtractor.execute(youtubeUrl)
    }

    fun downloadVideo(downloadUrlLink: String, fileName: String) {

        var downloadHandler: Handler?
        val downloadHandlerThread = HandlerThread(AppConstant.Handler.YOUTUBE_DOWNLOAD_HANDLER)
        downloadHandlerThread.also {
            it.start()
            downloadHandler = Handler(it.looper)
        }
        downloadHandler?.post {
            DownloadUtil(
                context = applicationContext,
                downloadUrl = downloadUrlLink,
                directoryName = "YoutubeVideos",
                fileName = fileName
            )
        }
    }

}