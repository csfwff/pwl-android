package com.xiamo.pwl.util


import android.content.Context
import android.content.Intent
import com.ayvytr.ktx.context.toast
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.BaseBean
import com.xiamo.pwl.bean.OpenRedpack
import com.xiamo.pwl.bean.UserInfo
import com.xiamo.pwl.bean.UserMeme
import com.xiamo.pwl.common.*
import com.xiamo.pwl.common.URL_SEND_MSG
import com.xiamo.pwl.ui.LoginActivity
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

    //登录
    fun login(
        context: Context,
        username:String,
        password:String,
        callback: ((String) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ){
        var params = HashMap<String,String>()
        params["nameOrEmail"] = username
        params["userPassword"] = StringToMD5.stringToMD5(password)

        OkGo.post<String>(BASE_URL + URL_GET_KEY)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    if(baseBean.code==0){
                        callback?.invoke(baseBean.Key!!)
                    }else{
                        errCallback?.invoke(baseBean.msg+"")
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
        content:String,
        callback: (() -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ){
        var params = HashMap<String,String>()
        params["apiKey"] = API_KEY
        params["content"] = content
        OkGo.post<String>(BASE_URL + URL_SEND_MSG)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    val baseBean = gson.fromJson(response?.body(), BaseBean::class.java)
                    when {
                        baseBean.code==0 -> {
                            callback?.invoke()
                        }
                        baseBean.msg == "401" -> {
                            //回到登录页
                            context.toast(R.string.toast_unauth)
                            val preferences by lazy { SharedPreferencesUtils(context) }
                            preferences.apiKey=""
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }
                        else -> {
                            errCallback?.invoke(baseBean.msg+"")
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
        oId:String,
        gesture:Int?=null,
        callback: ((OpenRedpack) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ){
        var params = HashMap<String,String>()
        params["apiKey"] = API_KEY
        params["oId"] = oId
        if(gesture!=null){
            params["gesture"] = gesture.toString()
        }
        OkGo.post<String>(BASE_URL + URL_OPEN_REDPACK)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val openRedpack = gson.fromJson(response?.body(),OpenRedpack::class.java)
                        if(openRedpack.code==null){
                            callback?.invoke(openRedpack)
                        }else{
                            errCallback?.invoke(openRedpack.msg.toString())
                        }


                    }catch (e:Exception){
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
        userId:String,
        callback: ((UserInfo) -> Unit)? = null,
        errCallback: ((String) -> Unit)? = null
    ){
        OkGo.get<String>("$BASE_URL$URL_GET_USER_INFO$userId?apiKey=$API_KEY")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val userInfo = gson.fromJson(response?.body(),UserInfo::class.java)
                        callback?.invoke(userInfo)
                    }catch (e:Exception){
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
    ){
        var params = HashMap<String,String>()
        params["apiKey"] = API_KEY
        params["gameId"] = "emojis"

        OkGo.post<String>(BASE_URL + URL_GET_USER_CLOUD)
            .upJson(gson.toJson(params))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val userMeme = gson.fromJson(response?.body(),UserMeme::class.java)
                        when {
                            userMeme.code==0 -> {
                                callback?.invoke(userMeme)
                            }
                            userMeme.msg == "401" -> {
                                //回到登录页
                                context.toast(R.string.toast_unauth)
                                val preferences by lazy { SharedPreferencesUtils(context) }
                                preferences.apiKey=""
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            }
                            else -> {
                                errCallback?.invoke(userMeme.msg+"")
                            }
                        }
                    }catch (e:Exception){
                        errCallback?.invoke(e.message.toString())
                    }
                }
                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    errCallback?.invoke(context.getString(R.string.toast_net_err))
                }
            })
    }




}