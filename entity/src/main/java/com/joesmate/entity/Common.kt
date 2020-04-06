package com.joesmate.entity

import java.util.concurrent.locks.ReentrantLock

/**
 * @author andrewliu
 * @create 2018/7/16
 * @Describe
 */
object Common {
    val SUCCEE_CODE = byteArrayOf(0x00, 0x00)
    val ERR_CODE = byteArrayOf(0x00, 0x01)
    val ACTION_BT_DATA = "action.a21.bt_data"
    val TAG_BT_IN_DATA = "bt_in_data_tag"
    val objLock = Any()//锁
    val backDataLock = Any()//返回数据的锁
    val lock= ReentrantLock()
}