package com.xiamo.pwl.ui


import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import co.zsmb.materialdrawerkt.builders.drawer
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.onClick
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.mikepenz.materialdrawer.Drawer
import com.xiamo.pwl.R
import kotlinx.android.synthetic.main.activity_main.*
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import com.xiamo.pwl.bean.ChatMessage
import com.xiamo.pwl.bean.RedPackMsg
import com.xiamo.pwl.common.*
import com.xiamo.pwl.util.FastBlurUtil
import com.xiamo.pwl.util.RequestUtil
import kotlinx.android.synthetic.main.activity_login.*

import okhttp3.OkHttpClient
import okhttp3.Response
import okio.ByteString
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    var drawer:Drawer?=null
    var wsManager:WsManager?=null
    var msgList = mutableListOf<ChatMessage>()
    var chatMsgAdapter :ChatMsgAdapter?=null
    var gson = Gson()
    var linearLayoutManager:LinearLayoutManager?=null


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
        val bgBmp = BitmapFactory.decodeResource(resources,R.mipmap.main_bg)
        val blurBmp = FastBlurUtil.toBlur(bgBmp,5)
        mainBg.setImageBitmap(blurBmp)


        initAdapter()
        initWs()

        headImg.onClick {
            drawer?.openDrawer()
        }
        sendBtn.onClick {
            sendMsg()
        }

    }

    fun sendMsg(){
        var content = contentEt.text.toString()
        if(content.isNullOrBlank()){
            toast(R.string.toast_say_something)
            return
        }

        sendBtn.startAnimation()
        RequestUtil.getInstance().sendMsg(this,content,{
            contentEt.setText("")
            sendBtn.doneLoadingAnimation(Color.parseColor("#3b3e43"),BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher))
            sendBtn.revertAnimation()
        },{
            toast(it)
            sendBtn.revertAnimation()
        })



    }


    fun initAdapter(){
        linearLayoutManager=  LinearLayoutManager(this)
        msgRv.layoutManager =linearLayoutManager
        chatMsgAdapter = ChatMsgAdapter(msgList)
        msgRv.adapter = chatMsgAdapter
        chatMsgAdapter?.initMarkdown()
    }

    private fun initWs(){
        val okHttpClient = OkHttpClient().newBuilder()
            .pingInterval(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
        wsManager = WsManager.Builder(this)
            .wsUrl("${BASE_WSS_URL}${URL_CHAT_ROOM}?apiKey=${API_KEY}")
            .needReconnect(true)
            .client(okHttpClient)
            .build()
        wsManager?.startConnect()
        wsManager?.setWsStatusListener(object :WsStatusListener(){
            override fun onMessage(text: String?) {
                super.onMessage(text)
                Log.e("-----",text.toString())
                var msg = gson.fromJson(text,ChatMessage::class.java)
                when(msg.type){
                    "msg"->addMsg(msg)
                    "revoke"->revokeMsg(msg)
                }

            }

            override fun onMessage(bytes: ByteString?) {
                super.onMessage(bytes)
            }

            override fun onOpen(response: Response?) {
                super.onOpen(response)
                toast(R.string.toast_connect_success)
            }

            override fun onReconnect() {
                super.onReconnect()
                toast(R.string.toast_reconnect)
            }

            override fun onClosing(code: Int, reason: String?) {
                super.onClosing(code, reason)
            }

            override fun onClosed(code: Int, reason: String?) {
                super.onClosed(code, reason)
            }

            override fun onFailure(t: Throwable?, response: Response?) {
                super.onFailure(t, response)
                toast(R.string.toast_connect_fail)
            }
        })
    }

    fun addMsg(msg: ChatMessage){
        if(msg.md!=null){
            if(msg.userName== USERNAME){
                msg.type="msgMine"
            }
            chatMsgAdapter?.addData(msg)
        }else{
            var redpack = gson.fromJson(msg.content,RedPackMsg::class.java)
            msg.redPackMsg = redpack
            msg.type = "redPacket"
            if(msg.userName== USERNAME){
                msg.type="redPacketMine"
            }
            chatMsgAdapter?.addData(msg)
        }
        scrollBottom()
    }

    fun revokeMsg(msg: ChatMessage){
        msgList.removeIf {
            it.oId == msg.oId
        }
        chatMsgAdapter?.notifyDataSetChanged()
    }


    fun scrollBottom(){
        var lastPos = linearLayoutManager?.findLastVisibleItemPosition()
        if(lastPos!=null){
            if(lastPos>=(msgList.size-2)){
                Handler().postDelayed({
                    msgRv.scrollToPosition(msgList.size-1)
                },1000)
            }
        }
    }


}