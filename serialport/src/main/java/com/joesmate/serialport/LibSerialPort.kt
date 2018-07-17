package com.jostmate.libserialport

/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
object LibSerialPort {
    /**
     * 打开串口
     */
    external fun device_open(path: String, baudrate: Int): Int

    /**
     * 设置波特率
     */
    external fun device_set_baud(fd: Int, baudrate: Int): Int

    /**
     * 关闭设备
     */
    external fun device_close(idecive: Int)

    /**
     * 读取数据（无条件返回所有收到的数据）
     */
    external fun device_read_all(idecive: Int, InputBuffer: ByteArray): Int

    /**
     * 写入数据
     */
    external fun device_write(idecive: Int, OutputRepor: ByteArray, len: Int): Int

    /**
     * 清空输入输出缓存
     */
    external fun flush(idevive: Int)

    /**
     * 取消操作
     */
    external fun Cancel()

    init {
        System.loadLibrary("serialport")
    }
}