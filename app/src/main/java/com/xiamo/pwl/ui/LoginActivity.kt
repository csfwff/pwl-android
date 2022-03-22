package com.xiamo.pwl.ui


import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.onClick
import com.gyf.immersionbar.ImmersionBar
import com.xiamo.pwl.R
import com.xiamo.pwl.common.API_KEY
import com.xiamo.pwl.util.FastBlurUtil
import com.xiamo.pwl.util.RequestUtil
import com.xiamo.pwl.util.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val preferences by lazy { SharedPreferencesUtils(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ImmersionBar.with(this).init()
        val bgBmp = BitmapFactory.decodeResource(resources,R.mipmap.login_bg)
        val blurBmp = FastBlurUtil.toBlur(bgBmp,10)
        bgImg.setImageBitmap(blurBmp)

        API_KEY = preferences.apiKey!!
        if(API_KEY.isNullOrEmpty()){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        loginBtn.onClick {
            login()
        }

    }

    private fun login(){
        var userName = userEt.text.toString().trim()
        if(userName.isNullOrEmpty()){
            toast(R.string.toast_user)
            return
        }
        var password = pwdEt.text.toString().trim()
        if(password.isNullOrEmpty()){
            toast(R.string.toast_pwd)
            return
        }

        loginBtn.startAnimation()
        RequestUtil.getInstance().login(this,userName,password,{
            loginBtn.doneLoadingAnimation(Color.parseColor("#3b3e43"),BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher))
            API_KEY = it
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },{
            toast(it)
            loginBtn.revertAnimation()
        })




    }
}