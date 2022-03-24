package com.xiamo.pwl.ui


import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import co.zsmb.materialdrawerkt.builders.drawer
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.onClick
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.mikepenz.materialdrawer.Drawer
import com.xiamo.pwl.R
import com.xiamo.pwl.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import com.xiamo.pwl.bean.ChatMessage
import com.xiamo.pwl.common.API_KEY
import com.xiamo.pwl.common.BASE_WSS_URL
import com.xiamo.pwl.common.URL_CHAT_ROOM

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
        initAdapter()
        initWs()

        headImg.onClick {
            drawer?.openDrawer()
        }

    }

    fun initAdapter(){
        msgRv.layoutManager = LinearLayoutManager(this)
        chatMsgAdapter = ChatMsgAdapter(msgList)
        msgRv.adapter = chatMsgAdapter
        chatMsgAdapter?.initMarkdown()
    }

    fun initWs(){
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
                if(msg.type=="msg"){
                    chatMsgAdapter?.addData(msg)
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


}