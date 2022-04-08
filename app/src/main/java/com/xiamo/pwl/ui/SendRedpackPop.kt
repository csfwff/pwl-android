package com.xiamo.pwl.ui

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.RedpackTabEntity
import razerdp.basepopup.BasePopupWindow
import java.util.ArrayList

class SendRedpackPop(context: Context):BasePopupWindow(context)  {
    var types = arrayOf("拼手气","普通","专属","心跳","猜拳")
    var tabL:CommonTabLayout?=null
    var userRecy:RecyclerView?=null
    var fingerRg:RadioGroup?=null
    var pointEt:EditText?=null
    var numEt:EditText?=null
    var infoEt:EditText?=null
    var sendBtn: CircularProgressButton?=null
    var cancelTv:TextView?=null





    init {
        contentView = createPopupById(R.layout.pop_send_redpack)
        tabL = contentView.findViewById(R.id.tabL)
        var tabList = mutableListOf<RedpackTabEntity>()
        types.forEach {
            tabList.add(RedpackTabEntity(it))
        }
        tabL?.setTabData(tabList as ArrayList<CustomTabEntity>)
        tabL?.setOnTabSelectListener(object :OnTabSelectListener{
            override fun onTabSelect(position: Int) {
                userRecy?.visibility = if(position == 2) View.VISIBLE else View.GONE
                numEt?.visibility = if(position == 0||position==1||position==3) View.VISIBLE else View.GONE
                fingerRg?.visibility = if(position == 4) View.VISIBLE else View.GONE
                pointEt?.hint =context.getString( if(position ==1) R.string.hint_redpack_point_1 else R.string.hint_redpack_point)

            }

            override fun onTabReselect(position: Int) {
            }

        })

        userRecy = contentView.findViewById(R.id.userRecy)
        fingerRg = contentView.findViewById(R.id.fingerRg)
        pointEt = contentView.findViewById(R.id.pointEt)
        pointEt = contentView.findViewById(R.id.pointEt)
        numEt = contentView.findViewById(R.id.numEt)
        infoEt = contentView.findViewById(R.id.infoEt)
        sendBtn = contentView.findViewById(R.id.sendBtn)
        cancelTv = contentView.findViewById(R.id.cancelTv)





    }
}