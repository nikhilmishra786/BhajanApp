package com.infomantri.bhakti.bhajan.Utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.infomantri.bhakti.bhajan.AppConstant
import java.io.File

class DownloadUtil(
    context: Context,
    downloadUrl: String,
    directoryName: String,
    fileName: String
) {

    private val TAG = "Download Task"
    private var mContext = context
    private var mDownloadUrl = downloadUrl
    private var mDownloadFileName = fileName
    private val mDirectoryName = directoryName

    init {
        downloadFiles()
    }

    fun downloadFiles() {

        val filePath: File? = mContext.getExternalFilesDir(mDirectoryName)

//        addStringToPreference(AppConstant.File.FILE_PATH, getExternalFilesDir(null)?.path)

        Log.v("FILE_PATH", ">>> File Path: $filePath")
        //create a new file, to save the downloaded file
        val file = File(filePath, mDownloadFileName)
        Log.v("FILE_PATH", ">>> File Path: -> ${filePath?.list()}")

        if (!file.isFile()) {
            val downloadManager =
                mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
            val downloadRequest = DownloadManager.Request(Uri.parse(mDownloadUrl))
                .setTitle(mDownloadFileName)
            downloadRequest.setDestinationUri(Uri.fromFile(file))
                .setDescription("Downloading Youtube Video...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(true)
                .setAllowedOverMetered(true)

            val downloadId = downloadManager?.enqueue(downloadRequest)
            downloadId?.let {
                Log.v("DownloadId", ">>> Download Id: $downloadId inside Download...")
            }

            Runnable {
                Toast.makeText(mContext, "Download started", Toast.LENGTH_SHORT).show()
            }
        }
    }

}