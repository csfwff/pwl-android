package com.xiamo.pwl.bean

import com.flyco.tablayout.listener.CustomTabEntity

class RedpackTabEntity(var title: String) :CustomTabEntity {


    override fun getTabTitle(): String {
        return title
    }

    override fun getTabSelectedIcon(): Int {
      return 0
    }

    override fun getTabUnselectedIcon(): Int {
        return 0
    }
}