package com.josemate.socketbt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.josemate.ibt.BaseBT
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * @author andrewliu
 * @create 2018/7/23
 * @Describe
 */
class SocketBT
constructor(private var mContext: Context?) : BaseBT {


    init {
        iniBtAdapter()
        // iniBtServerSocket();
    }


    private fun iniBtAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            throw Exception("蓝牙未找到")
        }

        //mBluetoothAdapter.disable();
        if (!mBluetoothAdapter!!.isEnabled) {
            Log.w("iniBtAdapter", "mBluetoothAdapter.isEnabled()=fales")
            mBluetoothAdapter!!.enable()
        }
    }

    override fun setContext(context: Context) {
        mContext = context
    }

    override fun openBt(): Int {

        mClosed = false
        if (mServerThread == null)
        //双判断加锁，保证线程安全
            synchronized(this) {
                if (mServerThread == null) {
                    mServerThread = ServerThread()
                }

            }
        if (!mServerThread!!.isAlive) {
            mServerThread!!.start()
        }
        return 0
    }

    override fun closeBt(): Int {
        mClosed = true
        object : Thread() {
            override fun run() {
                try {
                    if (mSocket != null) {
                        mSocket!!.close()
                        mSocket = null
                    }
                    if (mServerSocket != null) {
                        mServerSocket!!.close()
                        mServerSocket = null
                        mConneted = false
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                if (mServerThread != null) {
                    mServerThread!!.interrupt()
                    mServerThread = null
                }
            }
        }.start()
        return 0
    }


    override fun readBt(inputBuff: ByteArray): Int {
        var iRet = -1
        if (!mConneted || mServerSocket == null || mSocket == null || !mSocket!!.isConnected || `in` == null)
        //未连接
            return iRet

        try {
            iRet = `in`!!.read(inputBuff)
        } catch (ex: IOException) {
            `in`!!.close()
            out!!.close()
            mSocket!!.close()
            mConneted = false
            throw ex
        }

        return iRet
    }


    override fun writeBt(outputBuff: ByteArray, length: Int): Int {
        out!!.flush()
        out!!.write(outputBuff, 0, length)
        return length
    }


    override fun getIsConneted(): Boolean {
        return mConneted

    }

    inner class ServerThread : Thread() {
        init {
            try {
                iniBtServerSocket()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun run() {
            while (true) {
                if (mClosed)
                    break
                /* 接受客户端的连接请求 */
                try {
                    if (mServerSocket == null) {
                        iniBtServerSocket()
                    }
                    mSocket = mServerSocket!!.accept()
                    if (mSocket!!.isConnected) {
                        `in` = mSocket!!.inputStream
                        out = mSocket!!.outputStream
                        mConneted = true
                    }

                } catch (ex: Exception) {
                    mConneted = false
                    ex.printStackTrace()
                    //  SocketBT.this.iniBtServerSocket();
                }

                try {
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
    }

    companion object {
        val PROTOCOL_SCHEME_L2CAP = "btl2cap"
        val PROTOCOL_SCHEME_RFCOMM = "btspp"
        val PROTOCOL_SCHEME_BT_OBEX = "btgoep"
        val PROTOCOL_SCHEME_TCP_OBEX = "tcpobex"
        var mServerThread: ServerThread? = null
        var `in`: InputStream? = null
        var out: OutputStream? = null
        private var mConneted = false
        private var mClosed = true
        private var mServerSocket: BluetoothServerSocket? = null//蓝牙服务端socket
        private var mSocket: BluetoothSocket? = null// 蓝牙客户端socket

        private var mBluetoothAdapter: BluetoothAdapter? = null//蓝牙适配器


        private fun iniBtServerSocket() {


            try {
                val listenMethod = mBluetoothAdapter!!.javaClass.getMethod("listenUsingRfcommOn", Int::class.java)
                mServerSocket = listenMethod.invoke(mBluetoothAdapter, 29) as BluetoothServerSocket
            } catch (e: SecurityException) {

                // TODO Auto-generated catch block

                e.printStackTrace()

            } catch (e: IllegalArgumentException) {

                // TODO Auto-generated catch block

                e.printStackTrace()

            }

            //        catch (NoSuchMethodException e) {
            //
            //            // TODO Auto-generated catch block
            //
            //            e.printStackTrace();
            //
            //        } catch (IllegalAccessException e) {
            //
            //            // TODO Auto-generated catch block
            //
            //            e.printStackTrace();
            //
            //        } catch (InvocationTargetException e) {
            //
            //            // TODO Auto-generated catch block
            //
            //            e.printStackTrace();
            //
            //        }

        }
    }


}