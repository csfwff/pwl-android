package com.xiamo.pwl.bean

data class OpenRedpack(
    val info: Info,
    val recivers: List<String>,
    val who: List<Who>
)

data class Info(
    val count: Int,
    val got: Int,
    val msg: String,
    val userAvatarURL: String,
    val userName: String
)

data class Who(
    val avatar: String,
    val time: String,
    val userId: String,
    val userMoney: Int,
    val userName: String
)