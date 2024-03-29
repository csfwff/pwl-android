package com.xiamo.pwl.util


import android.content.Context
import android.content.Intent
import android.os.Build
import com.ayvytr.ktx.context.toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.xiamo.pwl.BuildConfig
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.*
import com.xiamo.pwl.common.*
import com.xiamo.pwl.common.URL_SEND_MSG
import com.xiamo.pwl.ui.activity.LoginActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RequestUtil private constructor() {

    var gson = Gson()

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    companion object {
        private var requestHelper: RequestUtil? = null
        fun getInstance(): RequestUtil {
            if (requestHelper == null) {
                requestHelper = RequestUtil()
            }
            return requestHelper!!
        }
    }


    fun toLogin(context: Context,url:String) {
        context.toast(context.getString(R.string.toast_unauth)+url)
        val preferences by lazy { SharedPreferencesUtils(context) }
        preferences.apiKey = ""
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    //登录
    fun login(
        context: Context,
        username: String,
        password: String,
        mfaCode: String,
        callback: ((String) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        var params = HashMap<String, String>()
        params["nameOrEmail"] = username
        params["mfaCode"] = mfaCode
        params["userPassword"] = StringToMD5.stringToMD5(password)

        OkGo.post<String>(BASE_URL + URL_GET_KEY)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    if (baseBean.code == 0) {
                        callback?.invoke(baseBean.Key!!)
                    } else {
                        errCallback?.invoke(baseBean.msg + "")
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun sendMsg(
        context: Context,
        content: String,
        callback: (() -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        var params = HashMap<String, String>()
        params["apiKey"] = API_KEY
        params["client"] = "Android/${BuildConfig.VERSION_NAME}"
        params["content"] = content
        OkGo.post<String>(BASE_URL + URL_SEND_MSG)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    when {
                        baseBean.code == 0 -> {
                            callback?.invoke()
                        }
                        baseBean.msg == "401" -> {
                            //回到登录页
                            toLogin(context,URL_SEND_MSG)
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg + "")
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun openRedpack(
        context: Context,
        oId: String,
        gesture: Int? = null,
        callback: ((OpenRedpack) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        var params = HashMap<String, String>()
        params["apiKey"] = API_KEY
        params["oId"] = oId
        if (gesture != null) {
            params["gesture"] = gesture.toString()
        }
        OkGo.post<String>(BASE_URL + URL_OPEN_REDPACK)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val openRedpack = gson.fromJson(response?.body(), OpenRedpack::class.java)
                        if (openRedpack.code == null) {
                            callback?.invoke(openRedpack)
                        } else {
                            errCallback?.invoke(openRedpack.msg.toString())
                        }


                    } catch (e: Exception) {
                        errCallback?.invoke(e.message.toString())
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun getUserInfo(
        context: Context,
        userId: String,
        callback: ((UserInfo) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        OkGo.get<String>("$BASE_URL$URL_GET_USER_INFO$userId?apiKey=$API_KEY")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val userInfo = gson.fromJson(response?.body(), UserInfo::class.java)
                        callback?.invoke(userInfo)
                    } catch (e: Exception) {
                        errCallback?.invoke(e.message.toString())
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }


    fun getUserMeme(
        context: Context,
        callback: ((UserMeme) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        var params = HashMap<String, String>()
        params["apiKey"] = API_KEY
        params["gameId"] = "emojis"

        OkGo.post<String>(BASE_URL + URL_GET_USER_CLOUD)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val userMeme = gson.fromJson(response?.body(), UserMeme::class.java)
                        when {
                            userMeme.code == 0 -> {
                                callback?.invoke(userMeme)
                            }
                            userMeme.msg == "401" -> {
                                //回到登录页
                                toLogin(context,URL_GET_USER_CLOUD)
                            }
                            else -> {
                                errCallback?.invoke(userMeme.msg + "")
                            }
                        }
                    } catch (e: Exception) {
                        errCallback?.invoke(e.message.toString())
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun uploadImg(
        context: Context,
        file: File,
        callback: ((String) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        var list = listOf(file)
        OkGo.post<String>("$BASE_URL$URL_UPLOAD_IMG?apiKey=$API_KEY")
            .addFileParams("file[]", list)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                        if (baseBean.code == 0) {
                            val uploadResult = gson.fromJson(baseBean.data, UploadFile::class.java)
                            if (uploadResult.errFiles.isNullOrEmpty()) {
                                //成功
                                var succMap = uploadResult.succMap
                                if (succMap != null) {
                                    var url = succMap.asJsonObject.get(file.name).asString
                                    callback?.invoke(url)
                                } else {
                                    errCallback?.invoke("上传失败")
                                }
                            } else {
                                errCallback?.invoke("上传失败")
                            }
                        } else {
                            errCallback?.invoke(baseBean.msg.toString())
                        }

                    } catch (e: Exception) {
                        errCallback?.invoke(e.message.toString())
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun syncMeme(
        context: Context,
        memeList: MutableList<String>,
        callback: (() -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {

        var params = HashMap<String, String>()
        params["apiKey"] = API_KEY
        params["gameId"] = "emojis"
        params["data"] = gson.toJson(memeList)
        OkGo.post<String>("$BASE_URL$URL_SYNC_USER_CLOUD")
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    when {
                        baseBean.code == 0 -> {
                            callback?.invoke()
                        }
                        baseBean.msg == "401" -> {
                            toLogin(context,URL_SYNC_USER_CLOUD)
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg.toString())
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun getHistoryMsg(
        context: Context,
        oId: String,
        callback: ((MutableList<ChatMessage>) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        OkGo.get<String>("$BASE_URL$URL_GET_HISTORY_MSG?apiKey=$API_KEY&oId=$oId&mode=1&size=25&type=md")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    when {
                        baseBean.code == 0 -> {
                            var list: MutableList<ChatMessage> = gson.fromJson(baseBean.data,
                                object : TypeToken<MutableList<ChatMessage>>() {}.type)
                            list.forEach {
                                if (it.content == null) {
                                    it.md = ""
                                    if (it.userName == USERNAME) {
                                        it.type = "msgMine"
                                    } else {
                                        it.type = "msg"
                                    }
                                } else {
                                    try {
                                        var redpack = gson.fromJson(it.content, RedPackMsg::class.java)
                                        it.redPackMsg = redpack
                                        if (it.userName == USERNAME) {
                                            it.type = "redPacketMine"
                                        } else {
                                            it.type = "redPacket"
                                        }
                                    } catch (e: Exception) {
                                        it.md = it.content
                                        if (it.userName == USERNAME) {
                                            it.type = "msgMine"
                                        } else {
                                            it.type = "msg"
                                        }
                                    }
                                }
                            }
                            list.reverse()
                            callback?.invoke(list)
                        }
                        baseBean.msg == "401" -> {
                            toLogin(context,URL_GET_HISTORY_MSG)
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg.toString())
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }


    fun getHistoryMsgOld(
        context: Context,
        callback: ((MutableList<ChatMessage>) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        OkGo.get<String>("$BASE_URL$URL_GET_HISTORY_MSG_OLD?apiKey=$API_KEY&page=1&type=md")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    when {
                        baseBean.code == 0 -> {
                            var list: MutableList<ChatMessage> = gson.fromJson(baseBean.data,
                                object : TypeToken<MutableList<ChatMessage>>() {}.type)
                            list.forEach {
                                if (it.content == null) {
                                    it.md = ""
                                    if (it.userName == USERNAME) {
                                        it.type = "msgMine"
                                    } else {
                                        it.type = "msg"
                                    }
                                } else {
                                    try {
                                        var redpack = gson.fromJson(it.content, RedPackMsg::class.java)
                                        it.redPackMsg = redpack
                                        if (it.userName == USERNAME) {
                                            it.type = "redPacketMine"
                                        } else {
                                            it.type = "redPacket"
                                        }
                                    } catch (e: Exception) {
                                        it.md = it.content
                                        if (it.userName == USERNAME) {
                                            it.type = "msgMine"
                                        } else {
                                            it.type = "msg"
                                        }
                                    }
                                }
                            }
                            list.reverse()
                            callback?.invoke(list)
                        }
                        baseBean.msg == "401" -> {
                            toLogin(context,URL_GET_HISTORY_MSG_OLD)
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg.toString())
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun getSmsCode(
        userName: String,
        userPhone: String,
        captcha: String,
        context: Context,
        callback: (() -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        var params = HashMap<String, String>()
        params["userName"] = userName
        params["userPhone"] = userPhone
        params["captcha"] = captcha
        OkGo.post<String>("$BASE_URL$URL_GET_SMS_CODE")
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    when (baseBean.code) {
                        0 -> {
                            callback?.invoke()
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg.toString())
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun verifySmsCode(
        smsCode: String,
        context: Context,
        callback: ((String) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ) {
        OkGo.get<String>("$BASE_URL$URL_VERIFY_SMS_CODE?code=$smsCode")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), VerifyCode::class.java)
                    when (baseBean.code) {
                        0 -> {
                            callback?.invoke(baseBean.userId!!)
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg.toString())
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun finishRegister(
        pwd: String,
        userAppRole:Int,
        userId:String,
        invite:String,
        context: Context,
        callback: (() -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ){
        var params = HashMap<String, String>()
        params["userAppRole"] = userAppRole.toString()
        params["userId"] = userId
        params["userPassword"] = StringToMD5.stringToMD5(pwd)
        OkGo.post<String>("$BASE_URL$URL_FINISH_REGISTER?r=$invite")
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    when (baseBean.code) {
                        0 -> {
                            callback?.invoke()
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg.toString())
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }

    fun getLiveness(
        context: Context,
        callback: ((Double) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ){
        OkGo.get<String>("$BASE_URL$URL_GET_LIVENESS?apiKey=$API_KEY")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        var liveness = gson.fromJson(response?.body(), Liveness::class.java)
                        if(liveness.liveness!=null){
                            callback?.invoke(liveness.liveness!!)
                        }else{
                            errCallback?.invoke("")
                        }
                    }catch (e:Exception){
                        errCallback?.invoke("")
                    }

                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }


}