package com.xiamo.pwl.ui

import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.User
import com.xiamo.pwl.common.PwlApplication

class UserAdapter(userList: MutableList<User>,canSel:Boolean): BaseQuickAdapter<User, BaseViewHolder>(R.layout.item_user) {
    private var canSel = canSel

    override fun convert(holder: BaseViewHolder, item: User) {
        if(item.userAvatarURL!!.endsWith("gif")){
            Glide.with(PwlApplication.instance!!.baseContext).asGif().load(item.userAvatarURL).into(holder.getView(R.id.headImg))
        }else{
            Glide.with(context).load(item.userAvatarURL).into(holder.getView(R.id.headImg))
        }
        if(canSel){
            if(item.selected){
                holder.setBackgroundResource(R.id.userBgView,R.drawable.bg_user_sel)
            }else{
                holder.setBackgroundColor(R.id.userBgView,ContextCompat.getColor(context,android.R.color.transparent))
            }
        }else{
            holder.setBackgroundColor(R.id.userBgView,ContextCompat.getColor(context,android.R.color.transparent))
        }
    }
}