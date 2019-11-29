package com.infomantri.bhakti.bhajan.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.infomantri.bhakti.bhajan.AppConstant
import com.infomantri.bhakti.bhajan.R
import com.infomantri.bhakti.bhajan.activities.MainActivity
import com.infomantri.bhakti.bhajan.activities.YoutubeVideosActivity
import com.infomantri.bhakti.bhajan.adapter.BhajanAdapter
import com.infomantri.bhakti.bhajan.data.model.Bhajan
import kotlinx.android.synthetic.main.fragment_bhajan.*
import java.util.*
import kotlin.collections.ArrayList

class BhajanFragment : Fragment(), TextToSpeech.OnInitListener {


    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mFirebaseReference: DatabaseReference
    lateinit var textToSpeech: TextToSpeech
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val language = textToSpeech.setLanguage(Locale.getDefault())
            speak()
        }
    }

    private fun speak() {
        Log.v(
            "Text_TO_SPEECH",
            ">>> Text to Speech Languages: ${textToSpeech.defaultEngine}"
        )
        textToSpeech.setSpeechRate(0.8f)
        textToSpeech.setPitch(1f)
        textToSpeech.speak(mText, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_bhajan, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseReference = mFirebaseDatabase.getReference(AppConstant.YOUTUBE_VIDEO)
        mFirebaseDatabase.getReference(AppConstant.IS_DOWNLOAD_AVAILABLE)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.getValue(Boolean::class.java) == true) {
//                        downloadYoutubeVideoData()
                    }
                    Log.v(
                        "IS_DOWNLOAD_AVAILABLE",
                        ">>> IS_DOWNLOAD_AVAILABLE : ${dataSnapshot.value}"
                    )
                }

            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setRecyclerView()

        textToSpeech = TextToSpeech(activity, this)
        textToSpeech.language = Locale.getDefault()

        btnTextToSpeech.setOnClickListener {
            mText = getString(R.string.dialogs)
            speak()
//            startVoiceRecognitionActivity()
//            downloadYoutubeVideoData()
        }

        bhajanList.withIndex().forEach { bhajan ->
            val youtubeVideo = mFirebaseReference.push().key
            youtubeVideo?.let {
                //                mFirebaseReference.child(bhajan.index.toString()).setValue(bhajanList[bhajan.index])
            }
        }
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    var mText = "Meri bhasa Hindi hai"
    lateinit var adapter: BhajanAdapter
    lateinit var bhajanList: ArrayList<Bhajan>
    fun setRecyclerView() {
        adapter = BhajanAdapter(text = {
            mText = it
            speak()
        }, playVideo = {
            if (it.isEmpty().not()) {
                val bundle = Bundle()
                bundle.apply {
                    putString(AppConstant.YOUTUBE_VIDEO_ID, it)
                }
                (activity as MainActivity).startActivityFromLeft(
                    YoutubeVideosActivity::class.java,
                    bundle
                )
            }
        })
        val linearLayoutManager = LinearLayoutManager(activity)
        rvBhajan.layoutManager = linearLayoutManager
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvBhajan.adapter = adapter
        bhajanList = arrayListOf(
            Bhajan(title = getString(R.string.hanuman_chalisa), videoId = "83Z5Hxk1xjs"),
            Bhajan(title = getString(R.string.shiv_chalisa), videoId = "83Z5Hxk1xjs"),
            Bhajan(title = getString(R.string.laksmhi_mata_pooja), videoId = "83Z5Hxk1xjs"),
            Bhajan(title = getString(R.string.hanuman_bhajan), videoId = "83Z5Hxk1xjs"),
            Bhajan(title = getString(R.string.shani_dev_chalisa), videoId = "83Z5Hxk1xjs"),
            Bhajan(title = getString(R.string.durga_chalisa), videoId = "83Z5Hxk1xjs"),
            Bhajan(title = getString(R.string.lakshmi_mata_bhajan), videoId = "83Z5Hxk1xjs")
        )

        adapter.submitList(bhajanList)
        adapter.notifyDataSetChanged()
    }

    private fun downloadYoutubeVideoData() {
        mFirebaseReference.limitToLast(2)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.v("LimitTOLAST", ">>> LimitTOLAST last n : ${dataSnapshot.value}")
                    val bhajanlist = dataSnapshot.children
                    bhajanlist.forEach {
                        it?.let {
                            bhajanList.add(it.getValue(Bhajan::class.java)!!)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(p0: DatabaseError) {
                }

            })

//        mFirebaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val bhajanlist = dataSnapshot.children
//                bhajanlist.forEach {
//                    it?.let {
//                        bhajanList.add(it.getValue(Bhajan::class.java)!!)
//                    }
//                }
//                adapter.notifyDataSetChanged()
//                Log.v(
//                    "FireBase_Update",
//                    ">>> FireBase data changed & downloaded successfully!!!... ${dataSnapshot.value}"
//                )
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e(TAG, "Failed to read app Bhajan data value.", error.toException())
//            }
//
//        })
    }

    private fun startVoiceRecognitionActivity() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Voice Recognition Demo..."
        )
        intent.putExtra(RecognizerIntent.EXTRA_WEB_SEARCH_ONLY, RecognizerIntent.ACTION_WEB_SEARCH)
        startActivityForResult(intent, 1234)
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            // Populate the wordsList with the String values the recognition
            // engine thought it heard
            val matches = data!!
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            matches.forEach {
                bhajanList.add(Bhajan(title = it))
                adapter.notifyDataSetChanged()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

}
