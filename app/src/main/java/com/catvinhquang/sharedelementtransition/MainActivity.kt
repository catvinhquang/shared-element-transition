package com.catvinhquang.sharedelementtransition

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair

/**
 * Created by Quang Cat on 16-Sep-2021
 **/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<View>(R.id.image)
        view.setOnClickListener {
            ActivityCompat.startActivity(
                this,
                Intent(this, DetailActivity::class.java),
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(view, DetailActivity.ELEMENT_IMAGE)
                ).toBundle()
            )
        }
    }
}