package com.catvinhquang.sharedelementtransition

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat

/**
 * Created by Quang Cat on 16-Sep-2021
 **/

class DetailActivity : AppCompatActivity() {

    companion object {
        const val ELEMENT_IMAGE = "detail:image"
    }

    private val contentReverseAnimator = AnimatorSet()
    private var downX = Float.NaN
    private var downY = Float.NaN
    private var allowReverseTransition = true
    private lateinit var content: View
    private lateinit var background: ColorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        background = window.decorView.background as? ColorDrawable ?: ColorDrawable(Color.BLACK)
        window.setBackgroundDrawable(background)
        content = findViewById<View>(android.R.id.content)
        content.pivotX = 0f

        val view = findViewById<View>(R.id.image)
        ViewCompat.setTransitionName(view, ELEMENT_IMAGE)

        // Need to do a long task?
        // ActivityCompat.postponeEnterTransition(this)
        // do something...
        // ActivityCompat.startPostponedEnterTransition(this)
    }

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        val isValidCoords = !downX.isNaN() && !downY.isNaN()
        val isValidAction = e.action == MotionEvent.ACTION_DOWN || isValidCoords
        if (isValidAction && !contentReverseAnimator.isRunning) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = e.x
                    downY = e.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val diff = e.y - downY
                    val thresholdDiff = content.height * 0.25f
                    if (diff >= 0) {
                        val alpha = 1 - (diff / thresholdDiff).coerceAtMost(1f)
                        background.alpha = (alpha * 255).toInt()
                        val scale = 0.8f + alpha * 0.2f
                        content.scaleX = scale
                        content.scaleY = scale
                        content.translationX = (1 - alpha) * content.width * 0.05f
                        content.translationY = diff
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val diff = e.y - downY
                    val thresholdDiff = content.height * 0.25f
                    val shouldFinish = diff >= thresholdDiff
                    if (shouldFinish) {
                        ActivityCompat.finishAfterTransition(this)
                    } else {
                        playReverseAnimation()
                    }
                    downX = Float.NaN
                    downY = Float.NaN
                }
            }
        }
        return super.dispatchTouchEvent(e)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            allowReverseTransition = !allowReverseTransition
            if (allowReverseTransition) {
                Toast.makeText(this, "Allow reverse transition", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Disallow reverse transition", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun finishAfterTransition() {
        if (allowReverseTransition) {
            super.finishAfterTransition()
        } else {
            super.finish()
        }
    }

    private fun playReverseAnimation() {
        contentReverseAnimator.cancel()
        contentReverseAnimator.playTogether(
            ObjectAnimator.ofInt(background, "alpha", background.alpha, 255),
            ObjectAnimator.ofFloat(content, "scaleX", content.scaleX, 1f),
            ObjectAnimator.ofFloat(content, "scaleY", content.scaleY, 1f),
            ObjectAnimator.ofFloat(content, "translationX", content.translationX, 0f),
            ObjectAnimator.ofFloat(content, "translationY", content.translationY, 0f)
        )
        contentReverseAnimator.start()
    }
}