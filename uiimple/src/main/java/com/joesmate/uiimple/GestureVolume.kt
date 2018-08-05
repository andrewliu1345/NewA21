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
    private val verticalMinistance = 100            //水平最小识别距离
    private val minVelocity = 10            //最小识别速度

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

//            var v = e1!!.getY() - e2!!.getY()//垂直
//            var h = e1!!.getX() - e2!!.getX()//水平

            if (e1!!.getX() - e2!!.getX() > verticalMinistance && Math.abs(velocityX) > minVelocity) {//向左滑

            } else if (e2!!.getX() - e1!!.getX() > verticalMinistance && Math.abs(velocityX) > minVelocity) {//向右滑

            } else if (e1!!.getY() - e2!!.getY() > verticalMinistance && Math.abs(velocityY) > minVelocity) {//向上滑
                mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                return true
            } else if (e2!!.getY() - e1!!.getY() > verticalMinistance && Math.abs(velocityY) > minVelocity) {//向下滑
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