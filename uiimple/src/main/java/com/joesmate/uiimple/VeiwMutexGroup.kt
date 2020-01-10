package com.joesmate.uiimple

import android.view.View

/*
Veiw 互斥组
 */
class VeiwMutexGroup {
    var viewlist: ArrayList<View> = ArrayList()


    /**
     * 添加
     */
    fun add(view: View) {
        viewlist.add(view)
    }

    /*
    清除
     */
    fun clear() {//
        viewlist.clear()
    }

    /**
     * 锁定当前按钮
     */
    fun lockSingle(view: View) {
        for (v in viewlist) {
            if (!v.equals(view))
                v.isEnabled = false
            else
                v.isEnabled = true
        }
    }

    fun lockAll() {
        for (v in viewlist)
            v.isEnabled = false
    }

    /**
     * 解锁所有按钮
     */
    fun unlockAll() {
        for (v in viewlist) {
            v.isEnabled = true
        }
    }

    fun unlockSingle(view: View) {
        for (v in viewlist) {
            if (!v.equals(view))
                v.isEnabled = true
            else
                v.isEnabled = false
        }
    }

    /**
     * 锁定当前按钮
     */
    fun lockMultiple(listv: ArrayList<View>) {
        for (v in viewlist) {
            var isE = false
            for (view in listv)
                if (v.equals(view))
                    isE = true
//            v.isEnabled = if (!isE) {
//                return false
//            } else {
//                return true
//            }

            if (!isE)
                v.isEnabled = false
            else
                v.isEnabled = true
        }
    }
}