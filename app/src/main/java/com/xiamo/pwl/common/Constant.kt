package com.xiamo.pwl.common

val BASE_URL = "https://fishpi.cn"
//val BASE_URL = "http://192.168.10.135:8080"
val BASE_WSS_URL = "wss://fishpi.cn"
val BASE_FILE_URL = "https://file.fishpi.cn"
var API_KEY = ""
var USERNAME = ""

const val URL_GET_KEY = "/api/getKey"
const val URL_CHAT_ROOM = "/chat-room-channel"
const val URL_SEND_MSG = "/chat-room/send"
const val URL_OPEN_REDPACK = "/chat-room/red-packet/open"
const val URL_GET_USER_INFO = "/user/"
const val URL_GET_USER_CLOUD = "/api/cloud/get"
const val URL_UPLOAD_IMG = "/upload"
const val URL_SYNC_USER_CLOUD = "/api/cloud/sync"
const val URL_GET_HISTORY_MSG = "/chat-room/getMessage"
const val URL_GET_HISTORY_MSG_OLD = "/chat-room/more"
const val URL_GET_CODE_IMG = "/captcha"
const val URL_GET_SMS_CODE = "/register"
const val URL_VERIFY_SMS_CODE = "/verify"
const val URL_FINISH_REGISTER = "/register2"
const val URL_GET_LIVENESS = "/user/liveness"

val MSG_TYPE_DEFAULT = 0
val MSG_TYPE_MSG = 1
val MSG_TYPE_ONLINE = 2
val MSG_TYPE_REVOKE = 3
val MSG_TYPE_REDPACK = 4
val MSG_TYPE_REDPACK_STATUS = 5
val MSG_TYPE_MSG_MINE = 6
val MSG_TYPE_REDPACK_MINE = 7
val MSG_TYPE_CHANGE_DISCUSS = 8