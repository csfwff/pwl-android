package com.xiamo.pwl.ui.adapter


import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.module.UpFetchModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.ChatMessage
import com.xiamo.pwl.common.*
import com.xiamo.pwl.plugin.GifGlideStore
import com.xiamo.pwl.plugin.ImageClickPlugin
import com.xiamo.pwl.util.HeadImgUtils
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin.GlideStore


class ChatMsgAdapter(msgList: MutableList<ChatMessage>): BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder>(msgList),UpFetchModule {

    var markdown:Markwon?=null

    init {
        addItemType(MSG_TYPE_MSG, R.layout.item_message)
        addItemType(MSG_TYPE_MSG_MINE, R.layout.item_message_mine)
//        addItemType(MSG_TYPE_ONLINE, R.layout.item_message)
//        addItemType(MSG_TYPE_REVOKE, R.layout.item_message)
        addItemType(MSG_TYPE_REDPACK, R.layout.item_msg_redpack)
        addItemType(MSG_TYPE_REDPACK_MINE, R.layout.item_msg_redpack_mine)
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
            .usePlugin(GlideImagesPlugin.create( GifGlideStore(Glide.with(context))))
            .usePlugin(ImageClickPlugin())
            .build()
    }

    override fun convert(holder: BaseViewHolder, item: ChatMessage) {
       when(holder.itemViewType){
           MSG_TYPE_MSG, MSG_TYPE_MSG_MINE->{
                setUserInfo(holder,item)
               //holder.setText(R.id.msgTv, Html.fromHtml(item.content,Html.FROM_HTML_MODE_COMPACT))
               markdown?.setMarkdown(holder.getView(R.id.msgTv),item.md!!)
           }
           MSG_TYPE_REDPACK,MSG_TYPE_REDPACK_MINE->{
               setUserInfo(holder,item)
               var redPackMsg = item.redPackMsg
               holder.setText(R.id.redpackContentTv,if(redPackMsg!!.msg.isNullOrBlank()) context.getString(getRedpackDefaultMsg(redPackMsg.type)) else redPackMsg.msg)
               holder.setText(R.id.redpackTypeTv,getRedpackType(redPackMsg.type))
               if(holder.itemViewType==MSG_TYPE_REDPACK){
                   holder.setVisible(R.id.fingerLl,redPackMsg.type=="rockPaperScissors")
               }
           }
       }
    }

    fun setUserInfo(holder: BaseViewHolder, item: ChatMessage){
        HeadImgUtils.loadHead(holder.getView(R.id.headImg),item.userAvatarURL!!)
        if(item.userNickname.isNullOrBlank()){
            holder.setText(R.id.userTv, item.userName)
        }else{
            holder.setText(R.id.userTv,"${item.userNickname}(${item.userName})")
        }
        holder.setText(R.id.timeTv,item.time)
    }


    fun getRedpackDefaultMsg(type:String?): Int {
        return when(type){
            "random"->R.string.redpack_random
            "average"->R.string.redpack_average
            "specify"->R.string.redpack_specify
            "heartbeat"->R.string.redpack_heartbeat
            "rockPaperScissors"->R.string.redpack_gesture
            else->R.string.redpack_default
        }
    }

    fun getRedpackType(type:String?): Int {
        return when(type){
            "random"->R.string.redpack_type_random
            "average"->R.string.redpack_type_average
            "specify"->R.string.redpack_type_specify
            "heartbeat"->R.string.redpack_type_heartbeat
            "rockPaperScissors"->R.string.redpack_type_gesture
            else->R.string.redpack_type_default
        }
    }

}