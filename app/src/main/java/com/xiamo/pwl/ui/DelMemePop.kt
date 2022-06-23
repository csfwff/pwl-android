package com.xiamo.pwl.ui;

import android.content.Context;
import android.widget.ImageView
import android.widget.TextView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.ayvytr.ktx.ui.onClick
import com.xiamo.pwl.R
import com.xiamo.pwl.util.HeadImgUtils
import razerdp.basepopup.BasePopupWindow

class DelMemePop(context:Context) : BasePopupWindow(context){
        var cancelTv: TextView?=null
        var sendBtn: CircularProgressButton?=null
        var image: ImageView?=null
        var pos:Int = -1
        init {
                contentView = createPopupById(R.layout.pop_del_meme)
                sendBtn = contentView.findViewById(R.id.sendBtn)
                cancelTv = contentView.findViewById(R.id.cancelTv)
                image = contentView.findViewById(R.id.image)

                cancelTv?.onClick {
                        dismiss()
                }

        }

        fun showImage(url:String,pos:Int){
                this.pos = pos
                HeadImgUtils.loadHead(image!!,url)
                showPopupWindow()
        }

        fun setOnConfirmListener(callback:(Int)->Unit){
                sendBtn?.onClick {
                        callback.invoke(this.pos)
                        dismiss()
                }
        }
}
