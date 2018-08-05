package com.joesmate.voice.baidutts

import android.content.Context
import android.os.Environment
import android.util.Log
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import com.joesmate.voice.ivoice.BaseVoice
import java.io.*

/**
 * @author andrewliu
 * @create 2018/8/6
 * @Describe
 */
class TextToSpeechForBaidu : BaseVoice {
    internal var m_context: Context
    private var mSpeechSynthesizer: SpeechSynthesizer? = null
    private var mSampleDirPath: String? = null

    companion object {
        private val SAMPLE_DIR_NAME = "A21Service_tts"
        private val SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat"
        private val SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat"
        private val TEXT_MODEL_NAME = "bd_etts_text.dat"

        private val ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat"
        private val ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat"
        private val ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat"
        private val APPID = "9902705"
    }

    constructor(context: Context) {
        m_context = context
        initialEnv()
        initialTts()
    }

    override fun doSpeek(text: String) {
        mSpeechSynthesizer?.stop()
        val result = mSpeechSynthesizer?.speak(text)
    }

    override fun setContext(context: Context) {
        m_context = context
    }

    override fun doClose() {
        mSpeechSynthesizer?.release()
    }

    private fun initialEnv() {
        if (mSampleDirPath == null) {
            val sdcardPath = Environment.getExternalStorageDirectory().toString()
            mSampleDirPath = "$sdcardPath/$SAMPLE_DIR_NAME"
        }
        makeDir(mSampleDirPath)
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, "$mSampleDirPath/$SPEECH_FEMALE_MODEL_NAME")
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, "$mSampleDirPath/$SPEECH_MALE_MODEL_NAME")
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, "$mSampleDirPath/$TEXT_MODEL_NAME")


        copyFromAssetsToSdcard(false, "english/$ENGLISH_SPEECH_FEMALE_MODEL_NAME",
                "$mSampleDirPath/$ENGLISH_SPEECH_FEMALE_MODEL_NAME")
        copyFromAssetsToSdcard(false, "english/$ENGLISH_SPEECH_MALE_MODEL_NAME",
                "$mSampleDirPath/$ENGLISH_SPEECH_MALE_MODEL_NAME")
        copyFromAssetsToSdcard(false, "english/$ENGLISH_TEXT_MODEL_NAME",
                "$mSampleDirPath/$ENGLISH_TEXT_MODEL_NAME")
    }

    private fun initialTts() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance()
        mSpeechSynthesizer?.setContext(m_context)
        mSpeechSynthesizer?.setSpeechSynthesizerListener(ttsSynListener)
        // 文本模型文件路径 (离线引擎使用)
        mSpeechSynthesizer?.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE,
                "$mSampleDirPath/$TEXT_MODEL_NAME")
        // 声学模型文件路径 (离线引擎使用)
        mSpeechSynthesizer?.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                "$mSampleDirPath/$SPEECH_FEMALE_MODEL_NAME")
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        // mSpeechSynthesizer?.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE,
        // mSampleDirPath + "/" + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        mSpeechSynthesizer?.setAppId(APPID)
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        //        mSpeechSynthesizer?.setApiKey("myqackCzY8USfRqCchAah1kW",
        //                "4f5c29f854bcf1d333c78117625155fa");
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        mSpeechSynthesizer?.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
        // 设置Mix模式的合成策略
        // mSpeechSynthesizer?.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(可以不使用，只是验证授权是否成功)
        val authInfo = mSpeechSynthesizer?.auth(TtsMode.MIX)
        if (authInfo!!.isSuccess) {
            Log.i("验证授权", "验证授权成功")
        } else {
            val errorMsg = authInfo.ttsError.detailMessage
            Log.i("验证授权", "验证授权失败msg:$errorMsg")
        }
        // 初始化tts
        mSpeechSynthesizer?.initTts(TtsMode.MIX)
        // 加载离线英文资源（提供离线英文合成功能）
        val result = mSpeechSynthesizer?.loadEnglishModel("$mSampleDirPath/$ENGLISH_TEXT_MODEL_NAME",
                "$mSampleDirPath/$ENGLISH_SPEECH_FEMALE_MODEL_NAME")


    }

    private fun makeDir(dirPath: String?) {
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private fun copyFromAssetsToSdcard(isCover: Boolean, source: String, dest: String) {
        val file = File(dest)
        if (isCover || !isCover && !file.exists()) {
            var _is: InputStream? = null
            var fos: FileOutputStream? = null
            try {
                _is = m_context.resources.assets.open(source)
                fos = FileOutputStream(dest)
                val buffer = ByteArray(1024)
                var size = 0

                while (true) {
                    size = _is!!.read(buffer, 0, 1024)
                    if (size >= 0)
                        fos.write(buffer, 0, size)
                    else
                        break
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                try {
                    _is?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private val ttsSynListener = object : SpeechSynthesizerListener {
        override fun onSynthesizeStart(s: String) {

        }

        override fun onSynthesizeDataArrived(s: String, bytes: ByteArray, i: Int) {

        }

        override fun onSynthesizeFinish(s: String) {

        }

        override fun onSpeechStart(s: String) {

        }

        override fun onSpeechProgressChanged(s: String, i: Int) {

        }

        override fun onSpeechFinish(s: String) {

        }

        override fun onError(s: String, speechError: SpeechError) {
            Log.d("onError error=$s", "--utteranceId=" + speechError.code)
        }
    }
}