package com.xiamo.pwl.ui


import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.ChatMessage
import com.xiamo.pwl.common.*

class ChatMsgAdapter(msgList: MutableList<ChatMessage>): BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder>(msgList) {

    init {
        addItemType(MSG_TYPE_MSG, R.layout.item_message)
        addItemType(MSG_TYPE_ONLINE, R.layout.item_message)
        addItemType(MSG_TYPE_REVOKE, R.layout.item_message)
        addItemType(MSG_TYPE_REDPACK, R.layout.item_message)
        addItemType(MSG_TYPE_DEFAULT, R.layout.item_message)
    }
    override fun convert(holder: BaseViewHolder, item: ChatMessage) {
       when(holder.itemViewType){
           MSG_TYPE_MSG->{
               Glide.with(context).load(item.userAvatarURL).into(holder.getView(R.id.headImg))
               holder.setText(R.id.msgTv,item.md)
           }
       }
    }
}