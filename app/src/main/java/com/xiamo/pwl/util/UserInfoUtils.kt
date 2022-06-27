package com.xiamo.pwl.util

import androidx.annotation.DrawableRes
import com.xiamo.pwl.R

object UserInfoUtils {

    @DrawableRes
    fun getUserRoleImg(role:String): Int{
        return when(role){
            "管理员"-> R.mipmap.admin_role
            "OP"->R.mipmap.op_role
            "超级会员"->R.mipmap.svip_role
            "纪律委员"->R.mipmap.police_role
            "成员"->R.mipmap.vip_role
            "新手"->R.mipmap.new_role
            else->R.mipmap.new_role
        }
    }
}