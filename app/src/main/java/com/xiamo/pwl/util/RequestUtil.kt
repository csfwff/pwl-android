package com.xiamo.pwl.util


import android.content.Context
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.BaseBean
import com.xiamo.pwl.common.API_KEY
import com.xiamo.pwl.common.BASE_URL
import com.xiamo.pwl.common.URL_GET_KEY
import com.xiamo.pwl.common.URL_SEND_MSG
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
                    if(baseBean.code==0){
                        callback?.invoke()
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


}