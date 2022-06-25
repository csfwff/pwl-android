package com.xiamo.pwl.ui.adapter


import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.User
import com.xiamo.pwl.util.HeadImgUtils

class UserAdapter(userList: MutableList<User>,canSel:Boolean): BaseQuickAdapter<User, BaseViewHolder>(R.layout.item_user) {
    private var canSel = canSel

    override fun convert(holder: BaseViewHolder, item: User) {
        HeadImgUtils.loadHead(holder.getView(R.id.headImg),item.userAvatarURL!!)
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