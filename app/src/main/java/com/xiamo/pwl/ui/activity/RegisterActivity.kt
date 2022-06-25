package com.xiamo.pwl.ui.activity

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.onClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gyf.immersionbar.ImmersionBar
import com.xiamo.pwl.R
import com.xiamo.pwl.common.BASE_URL
import com.xiamo.pwl.common.URL_GET_CODE_IMG
import com.xiamo.pwl.util.FastBlurUtil
import com.xiamo.pwl.util.RequestUtil
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.bgImg
import kotlinx.android.synthetic.main.activity_register.userEt

class RegisterActivity : AppCompatActivity() {


    var canSend = true
    private var timer  =object :CountDownTimer(60000,1000){
        override fun onTick(p0: Long) {
            getSmsBtn.text = "${p0/1000}s"
        }

        override fun onFinish() {
            canSend = true
            getSmsBtn.text = getString(R.string.btn_get_sms)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ImmersionBar.with(this).init()
        val bgBmp = BitmapFactory.decodeResource(resources,R.mipmap.main_bg_2)
        val blurBmp = FastBlurUtil.toBlur(bgBmp,10)
        bgImg.setImageBitmap(blurBmp)

        getCodeImg()
        codeImg.onClick {
            getCodeImg()
        }
        getSmsBtn.onClick {
            getSmsCode()
        }

        toLoginTv.onClick {
            finish()
        }

        registerBtn.onClick {
            verifySmsCode()
        }

    }


    fun getCodeImg(){
        Glide.with(this).load(BASE_URL+ URL_GET_CODE_IMG)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE).into(codeImg)
    }

    fun getSmsCode(){
        if(!canSend){
            return
        }
        var userName = userEt.text.toString().trim()
        if(userName.isBlank()){
            toast(R.string.toast_user_1)
            return
        }
        var phone = phoneEt.text.toString().trim()
        if(phone.isBlank()){
            toast(R.string.toast_phone)
            return
        }
        var code = codeEt.text.toString().trim()
        if(code.isBlank()){
            toast(R.string.toast_code)
            return
        }

        RequestUtil.getInstance().getSmsCode(userName,phone,code,this,{
            toast(R.string.toast_send_success)
            canSend = false
            timer.start()
        },{
            getCodeImg()
            codeEt.setText("")
            toast(it)
        })

    }

    fun verifySmsCode(){
        var smsCode = smsCodeEt.text.toString().trim()
        if(smsCode.isBlank()){
            toast(R.string.toast_sms_code)
            return
        }
        RequestUtil.getInstance().verifySmsCode(smsCode,this,{
            var intent = Intent(this,Register2Activity::class.java)
            intent.putExtra("userid",it)
            startActivity(intent)
            finish()
        },{
            toast(it)
        })
    }
}