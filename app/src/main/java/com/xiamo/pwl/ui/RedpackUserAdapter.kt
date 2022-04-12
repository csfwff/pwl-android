package com.xiamo.pwl.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.Who
import com.xiamo.pwl.util.HeadImgUtils

class RedpackUserAdapter(userList: MutableList<Who>): BaseQuickAdapter<Who, BaseViewHolder>(
    R.layout.item_open_redpack) {

    var maxPoint = 0
    fun setMaxNum(num:Int){
        maxPoint = num
    }

    override fun convert(holder: BaseViewHolder, item: Who) {
        HeadImgUtils.loadHead(holder.getView(R.id.headImg),item.avatar)
        holder.setText(R.id.userNameTv,item.userName)
        holder.setText(R.id.numTv,item.userMoney.toString())
        holder.setGone(R.id.zeroTv,item.userMoney!=0)
        holder.setGone(R.id.maxTv,item.userMoney!=maxPoint||item.userMoney==0)
    }
}