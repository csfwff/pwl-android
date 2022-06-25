package com.xiamo.pwl.ui.pop

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayvytr.ktx.ui.onClick
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.OpenRedpack
import com.xiamo.pwl.bean.Who
import com.xiamo.pwl.common.USERNAME
import com.xiamo.pwl.ui.adapter.RedpackUserAdapter
import com.xiamo.pwl.util.HeadImgUtils
import razerdp.basepopup.BasePopupWindow

class OpenRedpackPop(context: Context): BasePopupWindow(context) {

    var headImg:ImageView?=null
    var userNameTv:TextView?=null
    var infoTv:TextView?=null
    var numTv:TextView?=null
    var info1Tv:TextView?=null
    var userRecy:RecyclerView?=null
    var bgLl:LinearLayout?=null
    var userAdapter: RedpackUserAdapter?=null


    init {
        contentView = createPopupById(R.layout.pop_open_redpack)
        headImg = contentView.findViewById(R.id.headImg)
        userNameTv = contentView.findViewById(R.id.userNameTv)
        infoTv = contentView.findViewById(R.id.infoTv)
        numTv = contentView.findViewById(R.id.numTv)
        info1Tv = contentView.findViewById(R.id.info1Tv)
        userRecy = contentView.findViewById(R.id.userRecy)
        bgLl = contentView.findViewById(R.id.bgLl)
        userRecy?.layoutManager = LinearLayoutManager(context)
        userAdapter = RedpackUserAdapter(mutableListOf())
        userRecy?.adapter = userAdapter

        bgLl?.onClick {
            dismiss()
        }

    }

    fun setData(openRedpack: OpenRedpack): OpenRedpackPop {
        HeadImgUtils.loadHead(headImg!!,openRedpack.info!!.userAvatarURL)
        userNameTv?.text = "${openRedpack.info.userName}的红包"
        infoTv?.text = openRedpack.info.msg
        numTv?.text = "总计：${openRedpack.info.got}/${openRedpack.info.count}"
        info1Tv?.text = getRedpackMsg(openRedpack)
        userAdapter?.setMaxNum(getMax(openRedpack))
        userAdapter?.setNewInstance(openRedpack.who as MutableList<Who>)
        return this
    }

    fun getRedpackMsg(openRedpack: OpenRedpack): String {
        var hasGetWho = openRedpack.who!!.find {
            it.userId == USERNAME
        }
        return if(hasGetWho==null){
            //没抢到 或者不是专属
            if(openRedpack.recivers!!.isNotEmpty()){
                "会错意了"
            }else{
                "错过一个亿"
            }
        }else{
            "${hasGetWho.userMoney}积分"
        }
    }

    fun getMax(openRedpack: OpenRedpack): Int {
        return openRedpack.who!!.maxOf {
            it.userMoney
        }
    }
}