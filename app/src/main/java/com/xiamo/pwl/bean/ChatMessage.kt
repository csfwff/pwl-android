package com.xiamo.pwl.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.JsonElement
import com.xiamo.pwl.common.*

class ChatMessage : MultiItemEntity {


    var md:String?=null
    var userAvatarURL:String?=null
    var userNickname:String?=null
    var sysMetal:String?=null
    var time:String?=null
    var userName:String?=null
    var type:String?=null
    var content:JsonElement?=null
    var discussing:String?=null
    var onlineChatCnt:String?=null
    var whoGot:String?=null
    var whoGive:String?=null
    var count:String?=null
    var oId:String?=null
    var got:String?=null
    var users:List<User>?=null
    var redPackMsg:RedPackMsg?=null
    var newDiscuss:String?=null
    var whoChanged:String?=null
    override val itemType: Int
        get() = when(type){
            "msg"->MSG_TYPE_MSG
            "online"->MSG_TYPE_ONLINE
            "revoke"->MSG_TYPE_REVOKE
            "redPacket"->MSG_TYPE_REDPACK
            "redPacketStatus"-> MSG_TYPE_REDPACK_STATUS
            "msgMine"-> MSG_TYPE_MSG_MINE
            "redPacketMine"-> MSG_TYPE_REDPACK_MINE
            "discussChanged"->MSG_TYPE_CHANGE_DISCUSS
            else->MSG_TYPE_DEFAULT
        }
}

class User{
    var userAvatarURL:String?=null
    var userName:String?=null
    var homePage:String?=null
    var selected = false
}

class RedPackMsg{
    var msg:String?=null
    var recivers:String?=null
    var senderId:String?=null
    var msgType:String?=null
    var money:String?=null
    var count:String?=null
    var type:String?=null
    var got:String?=null
    var who:List<Who>?=null
}