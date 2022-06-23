package com.xiamo.pwl.ui

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.ayvytr.ktx.ui.onClick
import com.xiamo.pwl.R
import com.xiamo.pwl.util.HeadImgUtils
import razerdp.basepopup.BasePopupWindow

class BigImagePop (context: Context): BasePopupWindow(context) {
    var addTv: TextView?=null
    var image:ImageView?=null
    var url = ""
    init {
        contentView = createPopupById(R.layout.pop_imge)
        addTv = contentView.findViewById(R.id.addMemeTv)
        image = contentView.findViewById(R.id.image)
        contentView.onClick {
            dismiss()
        }
    }

    fun showImage(url:String){
        this.url = url
        HeadImgUtils.loadHead(image!!,url)
        showPopupWindow()
    }

    fun setOnConfirmListener(callback:(String)->Unit){
        addTv?.onClick {
            callback.invoke(this.url)
        }
    }

}