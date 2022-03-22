package com.xiamo.pwl.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.xiamo.pwl.common.*

class ChatMessage : MultiItemEntity {


    var md:String?=null
    var userAvatarURL:String?=null
    var userNickname:String?=null
    var sysMetal:String?=null
    var time:String?=null
    var userName:String?=null
    var type:String?=null
    var content:String?=null
    var discussing:String?=null
    var onlineChatCnt:String?=null
    var whoGot:String?=null
    var whoGive:String?=null
    var count:String?=null
    var oId:String?=null
    var got:String?=null
    var users:List<User>?=null
    override val itemType: Int
        get() = when(type){
            "msg"->MSG_TYPE_MSG
            "online"->MSG_TYPE_ONLINE
            "revoke"->MSG_TYPE_REVOKE
            "redPacketStatus"->MSG_TYPE_REDPACK
            else->MSG_TYPE_DEFAULT
        }
}

class User{
    var userAvatarURL:String?=null
    var userName:String?=null
    var homePage:String?=null
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
    var who:String?=null
}