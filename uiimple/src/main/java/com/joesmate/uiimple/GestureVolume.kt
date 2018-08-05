package com.joesmate.uiimple

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


/**
 * @author andrewliu
 * @create 2018/8/5
 * @Describe
 */
class GestureVolume : View {
    val FLIP_DISTANCE = 130
    var mAudioManager: AudioManager? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mDetector = GestureDetector(context, GestureListener)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mDetector!!.onTouchEvent(event)

    }

    var mDetector: GestureDetector? = null
    var GestureListener = object : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {

            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {

            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (e1!!.getX() - e2!!.getX() > FLIP_DISTANCE) {//向左滑

                return true
            }
            if (e2!!.getX() - e1!!.getX() > FLIP_DISTANCE) {//向右滑

                return true
            }
            if (e1!!.getY() - e2!!.getY() > FLIP_DISTANCE) {//向上滑
                mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                return true
            }
            if (e2!!.getY() - e1!!.getY() > FLIP_DISTANCE) {//向下滑
                mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                return true
            }

            return false

        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {

            return false
        }

        override fun onLongPress(e: MotionEvent?) {

        }

    }
}