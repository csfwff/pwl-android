package com.xiamo.pwl.common

val BASE_URL = "https://fishpi.cn"
val BASE_WSS_URL = "wss://fishpi.cn"
var API_KEY = ""
var USERNAME = ""

const val URL_GET_KEY = "/api/getKey"
const val URL_CHAT_ROOM = "/chat-room-channel"
const val URL_SEND_MSG = "/chat-room/send"

val MSG_TYPE_DEFAULT = 0
val MSG_TYPE_MSG = 1
val MSG_TYPE_ONLINE = 2
val MSG_TYPE_REVOKE = 3
val MSG_TYPE_REDPACK = 4
val MSG_TYPE_REDPACK_STATUS = 5
val MSG_TYPE_MSG_MINE = 6
val MSG_TYPE_REDPACK_MINE = 7