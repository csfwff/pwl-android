package com.xiamo.pwl.ui


import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.ChatMessage
import com.xiamo.pwl.common.*
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin.GlideStore


class ChatMsgAdapter(msgList: MutableList<ChatMessage>): BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder>(msgList) {

    var markdown:Markwon?=null

    init {
        addItemType(MSG_TYPE_MSG, R.layout.item_message)
        addItemType(MSG_TYPE_ONLINE, R.layout.item_message)
        addItemType(MSG_TYPE_REVOKE, R.layout.item_message)
        addItemType(MSG_TYPE_REDPACK, R.layout.item_message)
        addItemType(MSG_TYPE_DEFAULT, R.layout.item_message)

    }

    fun initMarkdown(){
        markdown = Markwon.builder(context) // automatically create Glide instance
            .usePlugin(GlideImagesPlugin.create(context)) // use supplied Glide instance
            .usePlugin(GlideImagesPlugin.create(Glide.with(context))) // if you need more control
            .usePlugin(GlideImagesPlugin.create(object : GlideStore {
                override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                    return Glide.with(context).load(drawable.destination)
                }

                override fun cancel(target: com.bumptech.glide.request.target.Target<*>) {
                    Glide.with(context).clear(target)
                }

            }))
            .build()
    }

    override fun convert(holder: BaseViewHolder, item: ChatMessage) {
       when(holder.itemViewType){
           MSG_TYPE_MSG->{
               Glide.with(context).load(item.userAvatarURL).into(holder.getView(R.id.headImg))
               //holder.setText(R.id.msgTv,item.md)
               //holder.setText(R.id.msgTv, Html.fromHtml(item.content,Html.FROM_HTML_MODE_COMPACT))
               markdown?.setMarkdown(holder.getView(R.id.msgTv),item.md!!)
           }
       }
    }
}