package com.xiamo.pwl.ui.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.util.HeadImgUtils

class UserMemeAdapter: BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_user_meme)  {

    override fun convert(holder: BaseViewHolder, item: String) {
        if(holder.layoutPosition==0){
            holder.setVisible(R.id.closeImg,false)
            Glide.with(context).load(R.mipmap.gallery).into(holder.getView(R.id.memeImg))
        } else if(holder.layoutPosition==1){
            holder.setVisible(R.id.closeImg,false)
            Glide.with(context).load(R.mipmap.camera).into(holder.getView(R.id.memeImg))
        }else{
            HeadImgUtils.loadHead(holder.getView(R.id.memeImg),item)
            holder.setVisible(R.id.closeImg,true)
        }

    }
}