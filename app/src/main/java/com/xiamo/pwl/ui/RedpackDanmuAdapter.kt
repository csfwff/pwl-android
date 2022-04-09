package com.xiamo.pwl.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.orzangleli.xdanmuku.XAdapter
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.RedpackDanmu
import android.widget.TextView


class RedpackDanmuAdapter(context: Context) : XAdapter<RedpackDanmu>() {
    var context = context
    override fun getView(p0: RedpackDanmu?, p1: View?): View {
        var holder1: ViewHolder1? = null
        var view: View? = null
        if (p1 == null) {
            view =
                LayoutInflater.from(context).inflate(R.layout.item_redpack_danmu, null)
            holder1 = ViewHolder1()
            holder1.content = view.findViewById(R.id.contentTv)
            view.setTag(holder1)

        } else {
            view = p1
            holder1 = p1.tag as ViewHolder1
            holder1.content = view.findViewById(R.id.contentTv)
        }
        holder1.content?.text =
            "${p0?.userName}抢到了${p0?.sendUserName}的红包(${p0?.getNum}/${p0?.totalNum})"
        return view!!
    }

    override fun getViewTypeArray(): IntArray {
        return intArrayOf(0)
    }

    override fun getSingleLineHeight(): Int {
        var view = LayoutInflater.from(context).inflate(R.layout.item_redpack_danmu, null);
        view.measure(0, 0);
        return view.measuredHeight
    }

    internal class ViewHolder1 {
        var content: TextView? = null
    }

}