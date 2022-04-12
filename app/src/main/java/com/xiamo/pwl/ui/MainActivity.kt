package com.xiamo.pwl.ui



import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.zsmb.materialdrawerkt.builders.drawer
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.isVisible
import com.ayvytr.ktx.ui.onClick
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.xiamo.pwl.R
import kotlinx.android.synthetic.main.activity_main.*
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import com.xiamo.pwl.bean.ChatMessage
import com.xiamo.pwl.bean.RedPackMsg
import com.xiamo.pwl.bean.RedpackDanmu
import com.xiamo.pwl.bean.User
import com.xiamo.pwl.common.*
import com.xiamo.pwl.util.FastBlurUtil
import com.xiamo.pwl.util.HeadImgUtils
import com.xiamo.pwl.util.RequestUtil

import okhttp3.OkHttpClient
import okhttp3.Response
import okio.ByteString
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    var drawer: Drawer? = null
    var wsManager: WsManager? = null
    var msgList = mutableListOf<ChatMessage>()
    var chatMsgAdapter: ChatMsgAdapter? = null
    var gson = Gson()
    var linearLayoutManager: LinearLayoutManager? = null

    var sendRedpackPop: SendRedpackPop? = null
    var userAdapter: UserAdapter? = null
    var userList = mutableListOf<User>()
    var openRedpackPop: OpenRedpackPop? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = drawer {
            savedInstance = savedInstanceState
            closeOnClick = true
            headerViewRes = R.layout.drawer_head
        }

        drawer?.apply {
            itemAdapter.add(PrimaryDrawerItem().withName(R.string.drawer_set))
            itemAdapter.add(PrimaryDrawerItem().withName(R.string.drawer_logout))
        }


        immersionBar {
            transparentBar()
            statusBarColor(android.R.color.transparent)
            statusBarView(R.id.bgImg)
        }
        val bgBmp = BitmapFactory.decodeResource(resources, R.mipmap.main_bg)
        val blurBmp = FastBlurUtil.toBlur(bgBmp, 5)
        mainBg.setImageBitmap(blurBmp)

        danmuView.setAdapter(RedpackDanmuAdapter(this))

        initAdapter()
        initWs()
        initClicks()

        getUserInfo()


    }

    fun sendMsg() {
        var content = contentEt.text.toString()
        if (content.isNullOrBlank()) {
            toast(R.string.toast_say_something)
            return
        }

        sendBtn.startAnimation()
        RequestUtil.getInstance().sendMsg(this, content, {
            contentEt.setText("")
            sendBtn.doneLoadingAnimation(
                Color.parseColor("#3b3e43"),
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            )
            sendBtn.revertAnimation()
        }, {
            toast(it)
            sendBtn.revertAnimation()
        })


    }

    fun initClicks() {
        headImg.onClick {
            drawer?.openDrawer()
        }

        titleTv.onClick {
            userRv.visibility = if (userRv.isVisible()) View.GONE else View.VISIBLE
        }


        sendBtn.onClick {
            sendMsg()
        }

        redpackImg.onClick {
            if (sendRedpackPop == null) {
                sendRedpackPop = SendRedpackPop(this)
                sendRedpackPop!!.setOnConfirmListener {
                    RequestUtil.getInstance().sendMsg(this, "[redpacket]${it}[/redpacket]", {

                    }, { result ->
                        toast(result)
                    })
                }
            }
            sendRedpackPop?.setUser(userAdapter?.data!!)?.showPopupWindow()
        }
    }


    fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(this)
        msgRv.layoutManager = linearLayoutManager
        chatMsgAdapter = ChatMsgAdapter(msgList)
        msgRv.adapter = chatMsgAdapter
        chatMsgAdapter?.initMarkdown()

        chatMsgAdapter?.addChildClickViewIds(
            R.id.redpackLl,
            R.id.finger0Tv,
            R.id.finger1Tv,
            R.id.finger2Tv
        )

        chatMsgAdapter?.setOnItemChildClickListener { adapter, view, position ->
            var msg = adapter.getItem(position) as ChatMessage
            when (view.id) {
                R.id.redpackLl -> {
                    openRedpack(msg.oId.toString())
                }
                R.id.finger0Tv -> {
                    openRedpack(msg.oId.toString(), 0)
                }
                R.id.finger1Tv -> {
                    openRedpack(msg.oId.toString(), 1)
                }
                R.id.finger2Tv -> {
                    openRedpack(msg.oId.toString(), 2)
                }
            }
        }

        userRv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        userAdapter = UserAdapter(userList, false)
        userRv.adapter = userAdapter

    }

    private fun initWs() {
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
        wsManager?.setWsStatusListener(object : WsStatusListener() {
            override fun onMessage(text: String?) {
                super.onMessage(text)
                Log.e("-----", text.toString())
                var msg = gson.fromJson(text, ChatMessage::class.java)
                when (msg.type) {
                    "msg" -> addMsg(msg)
                    "revoke" -> revokeMsg(msg)
                    "online" -> dealOnline(msg)
                    "redPacketStatus" -> addDanmu(msg)
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

    /**
     * 添加消息到列表
     * @param msg ChatMessage
     */
    fun addMsg(msg: ChatMessage) {
        if (msg.md != null) {
            if (msg.userName == USERNAME) {
                msg.type = "msgMine"
            }
            chatMsgAdapter?.addData(msg)
        } else {
            var redpack = gson.fromJson(msg.content, RedPackMsg::class.java)
            msg.redPackMsg = redpack
            msg.type = "redPacket"
            if (msg.userName == USERNAME) {
                msg.type = "redPacketMine"
            }
            chatMsgAdapter?.addData(msg)
        }
        scrollBottom()
    }

    /**
     * 撤回消息
     * @param msg ChatMessage
     */
    fun revokeMsg(msg: ChatMessage) {
        msgList.removeIf {
            it.oId == msg.oId
        }
        chatMsgAdapter?.notifyDataSetChanged()
    }

    /**
     * 滚动到底部
     */
    fun scrollBottom() {
        var lastPos = linearLayoutManager?.findLastVisibleItemPosition()
        if (lastPos != null) {
            if (lastPos >= (msgList.size - 2)) {
                Handler().postDelayed({
                    msgRv.scrollToPosition(msgList.size - 1)
                }, 1000)
            }
        }
    }

    fun dealOnline(msg: ChatMessage) {
        titleTv.text = "聊天室(${msg.onlineChatCnt})"
        userAdapter?.setNewInstance(msg.users as MutableList<User>?)
        discussTv.text = "#${msg.discussing}#"
    }

    fun addDanmu(msg: ChatMessage) {
        var redpackDanmu = RedpackDanmu().apply {
            userName = msg.whoGot.toString()
            sendUserName = msg.whoGive.toString()
            redpackId = msg.oId.toString()
            totalNum = msg.count.toString()
            getNum = msg.got.toString()
        }
        danmuView.addDanmu(redpackDanmu)
    }

    fun openRedpack(id: String, gesture: Int? = null) {
        RequestUtil.getInstance().openRedpack(this, id, gesture, {
            if (openRedpackPop == null) {
                openRedpackPop = OpenRedpackPop(this)
            }
            openRedpackPop?.setData(it)?.showPopupWindow()
        }, { result ->
            toast(result)
        })
    }

    fun getUserInfo() {
        RequestUtil.getInstance().getUserInfo(this, USERNAME, {
            val headView = drawer?.header
            val headImg = headView?.findViewById(R.id.headImg) as ImageView
            HeadImgUtils.loadHead(headImg, it.userAvatarURL)
            val bgImg =  headView.findViewById(R.id.bgImg) as ImageView
            HeadImgUtils.loadHead(bgImg, it.cardBg)
            (headView.findViewById(R.id.userNameTv) as TextView).text = it.userNickname
            (headView.findViewById(R.id.userIdTv) as TextView).text = it.userName
        }, { result ->
            toast(result)
        })
    }

}