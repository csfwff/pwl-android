package com.xiamo.pwl.ui

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.onClick
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.gson.Gson
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.RedpackSend
import com.xiamo.pwl.bean.RedpackTabEntity
import com.xiamo.pwl.bean.User
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
    var fingerScissorsRb:RadioButton?=null

    var userList = mutableListOf<User>()
    var userAdapter:UserAdapter?=null
    var gson = Gson()

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
                pointEt?.hint =context.getString( if(position ==1||position==2) R.string.hint_redpack_point_1 else R.string.hint_redpack_point)
                infoEt?.setText(getDefaultMsg(position))
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
        fingerScissorsRb = contentView.findViewById(R.id.fingerScissorsRb)

        userRecy?.layoutManager = GridLayoutManager(context,6)
        userAdapter = UserAdapter(userList,true)
        userRecy?.adapter = userAdapter
        userAdapter?.addChildClickViewIds(R.id.headImg)
        userAdapter?.setOnItemClickListener { adapter, view, position ->
            var user = adapter.getItem(position) as User
            user.selected = !user.selected
            adapter.notifyItemChanged(position)
        }

        cancelTv?.onClick {
            dismiss()
        }


    }

    fun setOnConfirmListener(callback:(String)->Unit){
        sendBtn?.onClick {
            var redpack = RedpackSend()
            var msg  = infoEt?.text.toString()
            redpack.msg = if(msg.isNotBlank()) msg else context.getString(getDefaultMsg(tabL!!.currentTab))
            redpack.type = getRedpackType(tabL!!.currentTab)
            redpack.money = pointEt?.text.toString()
            redpack.count = numEt?.text.toString()
            if(tabL!!.currentTab == 2){
                //专属红包
                    var selectedList = getSelectedUser()
                if(selectedList.isEmpty()){
                    context.toast(R.string.toast_specify)
                    return@onClick
                }
                redpack.recivers = gson.toJson(selectedList)
                redpack.count = selectedList.size.toString()
            }
            if(tabL!!.currentTab==4){
                //石头剪刀布
                redpack.gesture = when(fingerRg?.checkedRadioButtonId){
                    R.id.fingerStoneRb->0
                    R.id.fingerScissorsRb->1
                    R.id.fingerClothRb->2
                    else->0
                }.toString()
                redpack.count ="1"
            }
            callback.invoke(gson.toJson(redpack))
            dismiss()
        }
    }


    fun setUser(userList:MutableList<User>): SendRedpackPop {
        this.userList = userList
        userList.forEach {
            it.selected = false
        }
        userAdapter?.setNewInstance(userList)
        return this
    }

    fun getSelectedUser():MutableList<String>{
        var selected = mutableListOf<String>()
        this.userList.forEach {
            if(it.selected){
                selected.add(it.userName!!)
            }
        }
        return selected
    }

    override fun showPopupWindow() {
        super.showPopupWindow()
        tabL?.currentTab = 0
        numEt?.visibility =  View.VISIBLE
        numEt?.setText("2")
        pointEt?.setText("32")
        infoEt?.setText("摸鱼者，事竟成！")
        userRecy?.visibility = View.GONE
        fingerRg?.visibility = View.GONE
        fingerScissorsRb?.isChecked = true
    }


    fun getDefaultMsg(position:Int):Int{
        return when(position){
            0-> R.string.redpack_random
            1-> R.string.redpack_average
            2-> R.string.redpack_specify
            3-> R.string.redpack_heartbeat
            4-> R.string.redpack_gesture
            else-> R.string.redpack_default
        }
    }


    fun getRedpackType(position:Int):String{
        return  when(position){
            0->"random"
            1->"average"
            2->"specify"
            3->"heartbeat"
            4->"rockPaperScissors"
            else->"random"
        }
    }

}