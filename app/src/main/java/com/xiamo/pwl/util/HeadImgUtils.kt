package com.xiamo.pwl.util

import android.content.Context
import android.media.Image
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.xiamo.pwl.R
import com.xiamo.pwl.common.PwlApplication

object HeadImgUtils {

    fun loadHead(headImg: ImageView,url:String){
        if(url.endsWith("gif")){
            Glide.with(PwlApplication.instance!!.baseContext).asGif().load(url).into(headImg)
        }else{
            Glide.with(PwlApplication.instance!!.baseContext).load(url).into(headImg)
        }
    }
}