package com.xiamo.pwl.ui


import android.os.Bundle
import co.zsmb.materialdrawerkt.builders.drawer
import com.ayvytr.ktx.ui.onClick
import com.gyf.immersionbar.ktx.immersionBar
import com.mikepenz.materialdrawer.Drawer
import com.xiamo.pwl.R
import com.xiamo.pwl.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    var drawer:Drawer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = drawer {
            savedInstance = savedInstanceState
            closeOnClick = true
            headerViewRes= R.layout.drawer_head
        }

        immersionBar {
            transparentBar()
            statusBarColor(android.R.color.transparent)
            statusBarView(R.id.bgImg)
        }

        headImg.onClick {
            drawer?.openDrawer()
        }


    }
}