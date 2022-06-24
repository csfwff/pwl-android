package com.xiamo.pwl.ui



import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsProvider
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.zsmb.materialdrawerkt.builders.drawer
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.getContext
import com.ayvytr.ktx.ui.isVisible
import com.ayvytr.ktx.ui.onClick
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ktx.immersionBar
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.xiamo.pwl.R
import kotlinx.android.synthetic.main.activity_main.*
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import com.xiamo.pwl.bean.*
import com.xiamo.pwl.common.*
import com.xiamo.pwl.util.*

import okhttp3.OkHttpClient
import okhttp3.Response
import okio.ByteString
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.util.concurrent.TimeUnit
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.io.File
import java.util.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode

import org.greenrobot.eventbus.Subscribe



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
    var memeAdapter :UserMemeAdapter?=null

    var photoResult: ActivityResultLauncher<String>?=null
    var cameraResult: ActivityResultLauncher<Uri>?=null
    var imageSaveUri : Uri? = null

    var editTopicPop:EditTopicPop?=null
    var bigImagePop:BigImagePop?=null
    var delMemePop:DelMemePop?=null

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

        drawer?.onDrawerItemClickListener = object :Drawer.OnDrawerItemClickListener{
            override fun onItemClick(
                view: View?,
                position: Int,
                drawerItem: IDrawerItem<*>
            ): Boolean {
                when(position){
                    2->{  //退出登录
                        val preferences by lazy { SharedPreferencesUtils(this@MainActivity) }
                        preferences.apiKey=""
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        this@MainActivity.startActivity(intent)
                    }
                }
                return true
            }

        }


        immersionBar {
            transparentBar()
            statusBarColor(android.R.color.transparent)
            statusBarView(R.id.bgImg)
        }
        val bgBmp = BitmapFactory.decodeResource(resources, R.mipmap.main_bg_2)
        val blurBmp = FastBlurUtil.toBlur(bgBmp, 5)
        mainBg.setImageBitmap(blurBmp)

        danmuView.setAdapter(RedpackDanmuAdapter(this))

        photoResult = registerForActivityResult(ActivityResultContracts.GetContent()){
            uploadImg(it)
        }

        cameraResult = registerForActivityResult(ActivityResultContracts.TakePicture()){
            if (it){
                uploadImg(imageSaveUri!!)
            }
        }

        getEmojis()
        initAdapter()
        initWs()
        initClicks()
        initKeyboard()
        initMeme()

        getUserInfo()
        getUserMeme()
        getHistoryMsgOld()


    }

    fun sendMsg() {
        var content = contentEt.text.toString()
        if (content.isNullOrBlank()) {
            toast(R.string.toast_say_something)
            return
        }
      if(topicLl.visibility==View.VISIBLE){
            var topic = topicTv.text
            content += "\n*`# ${topic.subSequence(1,topic.length-1)} #`*"
        }

        sendBtn.startAnimation()
        RequestUtil.getInstance().sendMsg(this, content, {
            contentEt.setText("")
            sendBtn.doneLoadingAnimation(
                Color.parseColor("#3b3e43"),
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            )
            topicLl.visibility = View.INVISIBLE
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

        emojiImg.onClick {
            emojiRv.visibility = if (emojiRv.isVisible()) View.GONE else View.VISIBLE
            if(emojiRv.visibility == View.VISIBLE){
                UIUtil.hideKeyboard(this)
                memeRv.visibility = View.GONE
            }
        }

        photoImg.onClick {
            memeRv.visibility = if (memeRv.isVisible()) View.GONE else View.VISIBLE
            if(memeRv.visibility == View.VISIBLE){
                UIUtil.hideKeyboard(this)
                emojiRv.visibility = View.GONE
            }
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

        discussTv.onClick{
            topicLl.visibility = View.VISIBLE
            topicTv.text = discussTv.text
        }

        discussTv.setOnLongClickListener {
            if(editTopicPop==null){
                editTopicPop = EditTopicPop(this)
                editTopicPop!!.setOnConfirmListener {
                    RequestUtil.getInstance().sendMsg(this, "[setdiscuss]${it}[/setdiscuss]", {

                    }, { result ->
                        toast(result)
                    })
                }
            }
            editTopicPop?.showPopupWindow()
            true
        }

        //话题弹窗
        topicLl.onClick {
            topicLl.visibility = View.INVISIBLE
        }

        scrollBottomTv.onClick {
            msgRv.scrollToPosition(msgList.size - 1)
            scrollBottomTv.visibility = View.INVISIBLE
        }


    }

    fun initKeyboard(){
        setEventListener(this, KeyboardVisibilityEventListener {
                if(it){
                    emojiRv.visibility = View.GONE
                    memeRv.visibility = View.GONE
                }
            })
    }


    fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(this)
        msgRv.layoutManager = linearLayoutManager
        chatMsgAdapter = ChatMsgAdapter(msgList)
        msgRv.adapter = chatMsgAdapter
        chatMsgAdapter?.initMarkdown()
        msgRv.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    var lastPos = linearLayoutManager?.findLastVisibleItemPosition()
                    if (lastPos != null) {
                        if (lastPos >= (msgList.size - 2)) {
                            scrollBottomTv.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        })

        chatMsgAdapter?.upFetchModule?.isUpFetchEnable = true
        chatMsgAdapter?.upFetchModule?.setOnUpFetchListener {
            getHistoryMsg()
        }


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
                    "discussChanged"-> changeDiscuss(msg)
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
     * 话题修改
     * @param msg ChatMessage
     */
    fun changeDiscuss(msg: ChatMessage){
        discussTv.text = "#${msg.newDiscuss}#"
    }

    /**
     * 滚动到底部
     */
    fun scrollBottom() {
        var lastPos = linearLayoutManager?.findLastVisibleItemPosition()
        if (lastPos != null) {
            if (lastPos >= (msgList.size - 2)) {
                msgRv.scrollToPosition(msgList.size - 1)
                scrollBottomTv.visibility = View.INVISIBLE
            }else{
                scrollBottomTv.visibility = View.VISIBLE
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

    fun initMeme(){
        memeAdapter = UserMemeAdapter()
        memeRv.layoutManager = GridLayoutManager(this,5)
        memeRv.adapter = memeAdapter

        memeAdapter?.addChildClickViewIds(R.id.closeImg)
        memeAdapter?.setOnItemClickListener { adapter, view, position ->
            if(position == 0){
                //选择文件图片
                photoResult?.launch("image/*")
            } else  if(position == 1){
            //拍照
                onTakePicture()
            }else{
                //插入到输入框
                var item = adapter.getItem(position) as String
                var selStart = contentEt.selectionStart
                var selend = contentEt.selectionEnd
                var content = contentEt.text.delete(selStart,selend)
                content = content.insert(selStart,"![图片表情](${item})")
                contentEt.text = content
                contentEt.setSelection(selStart+item.length+9)
            }
        }
        memeAdapter?.setOnItemChildClickListener { adapter, view, position ->
            when(view.id){
                R.id.closeImg->{
                    if(delMemePop==null){
                        delMemePop = DelMemePop(this)
                        delMemePop!!.setOnConfirmListener {
                            //确认删除
                            var memeList = mutableListOf<String>()
                            memeAdapter?.data?.let { memeList.addAll(it) }
                            if(memeList.isNotEmpty()){
                                memeList.removeAt(position)
                                memeList.removeAt(0)
                                memeList.removeAt(0)
                            }
                            RequestUtil.getInstance().syncMeme(this,memeList,{
                                //删除成功
                                toast(R.string.toast_success)
                                memeAdapter?.removeAt(position)
                            },{
                                toast(it)
                            })
                        }
                    }
                    delMemePop?.showImage(adapter.getItem(position) as String,position)
                }
            }
        }

    }

    fun getUserMeme(){
        RequestUtil.getInstance().getUserMeme(this, {
            var memeList = gson.fromJson<List<String>>(it.data, object :TypeToken<List<String>>() {}.type)as MutableList<String>
            memeList.add(0,"")
            memeList.add(0,"")
            memeAdapter?.setNewInstance(memeList )
        }, { result ->
            toast(result)
        })
    }

    fun getEmojis() {
        var result: String = ""
        try {
            val inputReader = InputStreamReader(resources.assets.open("emojis.txt"))
            val bufReader = BufferedReader(inputReader)
            var line: String? = ""
            while (bufReader.readLine().also { line = it } != null) result += line
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(result.isBlank()){
            return
        }
        var emojisList = gson.fromJson<List<EmojiItem>>(result, object :TypeToken<List<EmojiItem>>() {}.type)
        var emojiAdapter = EmojiAdapter()
        emojiRv.layoutManager = GridLayoutManager(this,8)
        emojiAdapter.setNewInstance(emojisList as MutableList<EmojiItem>)
        emojiAdapter.setOnItemClickListener { adapter, view, position ->
            var item = adapter.getItem(position) as EmojiItem
            var selStart = contentEt.selectionStart
            var selend = contentEt.selectionEnd
            var content = contentEt.text.delete(selStart,selend)
            content = content.insert(selStart,":${item.name}:")
            contentEt.text = content
            contentEt.setSelection(selStart+item.name.length+2)
        }
        emojiRv.adapter = emojiAdapter


    }

    fun onTakePicture() {
        var fileName = "pwl_${System.currentTimeMillis()}.jpg"
        imageSaveUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                File(externalCacheDir!!.absolutePath, fileName)
            )
        }
        cameraResult?.launch(imageSaveUri)
    }

    fun uploadImg(uri:Uri){
        var file = File(UriUtils.getFileAbsolutePath(this,uri))
        RequestUtil.getInstance().uploadImg(this,file,{
            var selStart = contentEt.selectionStart
            var selend = contentEt.selectionEnd
            var content = contentEt.text.delete(selStart,selend)
            content = content.insert(selStart,"![图片表情](${it})")
            contentEt.text = content
            contentEt.setSelection(selStart+it.length+9)
        },{
            toast(it)
        })

    }


    fun addMeme(url:String){
        var memeList = mutableListOf<String>()
        memeAdapter?.data?.let { memeList.addAll(it) }
        if(memeList.isNotEmpty()){
            memeList.removeAt(0)
            memeList.removeAt(0)
        }
        if(memeList.indexOf(url)>=0){
            toast(R.string.toast_add_meme)
            return
        }
        memeList.add(url)
        RequestUtil.getInstance().syncMeme(this,memeList,{
            //添加成功
            toast(R.string.toast_success)
            memeAdapter?.addData(url)
        },{
            toast(it)
        })

    }

    fun getHistoryMsg(){
        if(chatMsgAdapter!!.itemCount==0){
            chatMsgAdapter?.upFetchModule?.isUpFetching = false
            return
        }
        var oId = chatMsgAdapter!!.getItem(0).oId
        RequestUtil.getInstance().getHistoryMsg(this,oId!!,{
            chatMsgAdapter?.addData(0,it)
        },{
            toast(it)
        })
    }

    fun getHistoryMsgOld(){
        RequestUtil.getInstance().getHistoryMsgOld(this,{
            chatMsgAdapter?.addData(0,it)
            msgRv.scrollToPosition(msgList.size - 1)
        },{
            toast(it)
        })
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PwlEvent) {
        when(event.code){
            1->{
                if(bigImagePop==null){
                    bigImagePop = BigImagePop(this)
                    bigImagePop!!.setOnConfirmListener {
                        addMeme(it)
                    }
                }
                bigImagePop?.showImage(event.info)
            }
        }
    }

}