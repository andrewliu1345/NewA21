package com.joesmate.voice.androidtts

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import com.joesmate.voice.ivoice.BaseVoice
import java.util.*

/**
 * @author andrewliu
 * @create 2018/8/6
 * @Describe
 */
class TextToSpeechForAndroid : BaseVoice {
    internal var m_context: Context?
    private var tts: TextToSpeech? = null

    constructor(context: Context?) {
        m_context = context
        tts = TextToSpeech(m_context, TTS_InitListener)
    }

    internal var TTS_InitListener: TextToSpeech.OnInitListener = TextToSpeech.OnInitListener { status ->
        Log.i("TextToSpeechForAndroid","InitListener status ${status}")
        if (status == TextToSpeech.SUCCESS) {

            //  设置中文朗读
            val result = tts?.setLanguage(Locale.CHINA)
            //  若不支持所设置的语言
            if (result != TextToSpeech.LANG_AVAILABLE && result != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                Log.e("TextToSpeechForAndroid", "不支持所设置的语言")
            }
        }
    }

    override fun doSpeek(text: String?) {
        if (Build.VERSION.SDK_INT > 21) {

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speech")

        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun setContext(context: Context?) {
        m_context = context
    }

    override fun doClose() {
        tts?.shutdown()
    }
}