package com.xiamo.pwl.ui


import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.xiamo.pwl.R
import com.xiamo.pwl.util.FastBlurUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ImmersionBar.with(this).init()
        val bgBmp = BitmapFactory.decodeResource(resources,R.mipmap.login_bg)
        val blurBmp = FastBlurUtil.toBlur(bgBmp,10)
        bgImg.setImageBitmap(blurBmp)


    }
}