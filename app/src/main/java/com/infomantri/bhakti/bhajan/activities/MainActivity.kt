package com.infomantri.bhakti.bhajan.activities

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.infomantri.bhakti.bhajan.BaseActivity
import com.infomantri.bhakti.bhajan.R
import com.infomantri.bhakti.bhajan.fragment.BhajanFragment
import kotlinx.android.synthetic.main.custom_toolbar.*


class MainActivity : BaseActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    val YoutubeAPIKey = "AIzaSyA9y2AKALhQS8e7BlY1HFk72flEh60HDD8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        setToolbar(getString(R.string.bhakti_bhajan))
        addFragment(BhajanFragment(), getString(R.string.bhajan))
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun setToolbar(title: String) {

        toolbar.setToolbar(
            titleColor = R.color.white,
            title = title,
            bgColor = R.color.colorPrimary
        )
    }


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.navigation_bhajan -> {
                    fragment = BhajanFragment()
                    addFragment(fragment, getString(R.string.bhajan))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_youtube -> {
                    startActivityFromLeft(YoutubeVideosActivity::class.java)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_dashboard -> {
                    startActivityFromLeft(DashBoardActivity::class.java)
                }

            }
            false
        }

    private fun addFragment(fragment: Fragment, title: String) {
        setToolbar(title)
        val transaction = supportFragmentManager.beginTransaction()
        val existingFragment = supportFragmentManager.findFragmentByTag(fragment.tag)
        transaction.replace(R.id.flHomeContainer, fragment)
            .commit()
    }

}
