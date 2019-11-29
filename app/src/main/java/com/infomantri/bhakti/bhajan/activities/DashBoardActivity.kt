package com.infomantri.bhakti.bhajan.activities

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.infomantri.bhakti.bhajan.AppConstant
import com.infomantri.bhakti.bhajan.BaseActivity
import com.infomantri.bhakti.bhajan.R
import com.infomantri.bhakti.bhajan.database.YoutubeRoomDatabase
import com.infomantri.bhakti.bhajan.database.YoutubeVideoDB
import com.infomantri.bhakti.bhajan.database.YoutubeVideoRepository
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class DashBoardActivity : BaseActivity() {

    var mVideoId = ""
    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mFirebaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        toolbar.setToolbar(
            titleColor = R.color.white,
            title = getString(R.string.title_dashboard),
            bgColor = R.color.colorPrimary
        )

        setOnClickListener()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseReference = mFirebaseDatabase.getReference(AppConstant.YOUTUBE_VIDEO)
    }

    private fun setOnClickListener() {
        fabSaveVideoUrl.setOnClickListener {
            if (mVideoId == "") {
                Toast.makeText(this, "Invalid Video URL", Toast.LENGTH_SHORT).show()
            } else {
                saveToRoomDatabase()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val clipBoardData = getClipBoardData()
        etPasteVideoUrl.setText(clipBoardData)
        mVideoId = getVideoId(clipBoardData)

    }

    private fun getVideoId(url: String): String {
        var videoId = ""
        var videoList: List<String>
        val supportedUrls =
            arrayOf(
                "https://youtu.be/",
                "https://m.youtube.com/watch?v=",
                "https://youtube.com/watch?v="
            )
//            arrayOf("youtu.be/", "m.youtube.com/", "youtube.com/", "www.youtube.com/")
        supportedUrls.forEach {
            if (url.contains(it)) {
                videoList = url.split(it)
                if (videoList.isEmpty().not() && videoList[videoList.size - 1].length <= 11) {
                    Log.v("VideoId", ">>> VideoId: ${videoList[videoList.size - 1]} url: $url")
                    return videoList[videoList.size - 1]
                }
            }
        }
        Log.v("VideoId", ">>> VideoId: $videoId url: $url")
        return videoId
    }

    private fun getClipBoardData(): String {
        val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipBoardManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    private fun saveToRoomDatabase() {
        var insertHandler: Handler?
        val insertHandlerThread = HandlerThread(AppConstant.Handler.YOUTUBE_INSERT_HANDLER)
        insertHandlerThread.also {
            it.start()
            insertHandler = Handler(it.looper)
        }
        insertHandler?.post {
            val msgDao = YoutubeRoomDatabase.getDatabase(application).youtubeVideoDao()
            val repository = YoutubeVideoRepository(msgDao)
            repository.insertVideo(YoutubeVideoDB(videoId = mVideoId))
        }

        var getHandler: Handler?
        var getHandlerThread = HandlerThread(AppConstant.Handler.YOUTUBE_GET_HANDLER)
        getHandlerThread.also {
            it.start()
            getHandler = Handler(it.looper)
        }
        getHandler?.post {
            val msgDao = YoutubeRoomDatabase.getDatabase(application).youtubeVideoDao()
            val repository = YoutubeVideoRepository(msgDao, videoStatus = false)
            val youtubeVideosList = repository.videosByStatuses
            Log.v("Videos_List", ">>> Youtube Videos List : $youtubeVideosList")

            if (youtubeVideosList.size >= 5) {
                mFirebaseDatabase.getReference(AppConstant.IS_DOWNLOAD_AVAILABLE).setValue(true)
                youtubeVideosList.forEach {
                    mFirebaseReference.child(it.id.toString()).setValue(it)
                }
            } else {
                mFirebaseDatabase.getReference(AppConstant.IS_DOWNLOAD_AVAILABLE).setValue(false)
            }
        }
    }

}
