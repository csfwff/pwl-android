package com.xiamo.pwl.ui.activity

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.onClick
import com.gyf.immersionbar.ImmersionBar
import com.xiamo.pwl.R
import com.xiamo.pwl.util.FastBlurUtil
import com.xiamo.pwl.util.RequestUtil
import kotlinx.android.synthetic.main.activity_register2.*
import kotlinx.android.synthetic.main.activity_register2.bgImg
import kotlinx.android.synthetic.main.activity_register2.pwdEt

class Register2Activity : AppCompatActivity() {
    var userId :String?= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)

        ImmersionBar.with(this).init()
        val bgBmp = BitmapFactory.decodeResource(resources,R.mipmap.main_bg_2)
        val blurBmp = FastBlurUtil.toBlur(bgBmp,10)
        bgImg.setImageBitmap(blurBmp)

        userId = intent.getStringExtra("userid")

        toLoginTv.onClick {
            finish()
        }

        registerBtn.onClick {
            register()
        }

    }

    fun register(){
        var password = pwdEt.text.toString().trim()
        if(password.isBlank()){
            toast(R.string.toast_pwd_1)
            return
        }
        var invite = inviteEt.text.toString().trim()
        if(invite.isBlank()){
            invite = "csfwff"
        }
        var role = if(hackerRb.isChecked) 0 else 1
        RequestUtil.getInstance().finishRegister(password,role,userId.toString(),invite,this,{
            toast(R.string.toast_register_success)
            finish()
        },{
            toast(it)
        })

    }
}