package com.xiamo.pwl.ui

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.ayvytr.ktx.context.toast
import com.ayvytr.ktx.ui.onClick
import com.xiamo.pwl.R
import razerdp.basepopup.BasePopupWindow

class EditTopicPop (context: Context): BasePopupWindow(context) {
    var infoEt: EditText?=null
    var cancelTv: TextView?=null
    var sendBtn: CircularProgressButton?=null


    init {
        contentView = createPopupById(R.layout.pop_edit_topic)
        infoEt = contentView.findViewById(R.id.infoEt)
        sendBtn = contentView.findViewById(R.id.sendBtn)
        cancelTv = contentView.findViewById(R.id.cancelTv)


        cancelTv?.onClick {
            dismiss()
        }

    }

    fun setOnConfirmListener(callback:(String)->Unit){
        sendBtn?.onClick {
            var topic  = infoEt?.text.toString()
            if (topic.isNullOrBlank()){
                context.toast(R.string.toast_topic)
                return@onClick
            }
            callback.invoke(topic)
            dismiss()
        }
    }

    override fun showPopupWindow() {
        super.showPopupWindow()
        infoEt?.setText("")
    }
}