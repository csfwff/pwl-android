package com.xiamo.pwl.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayvytr.ktx.context.toast
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.Medal
import com.xiamo.pwl.bean.MedalItem
import com.xiamo.pwl.util.HeadImgUtils
import com.xiamo.pwl.util.RequestUtil
import com.xiamo.pwl.util.UserInfoUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : AppCompatActivity() {

    var userName:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        userName = intent.getStringExtra("username")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = userName

        medalRv.layoutManager = LinearLayoutManager(this)

        getUserInfo()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Toolbar的事件---返回
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getUserInfo() {
        if (userName==null){
            return
        }
        RequestUtil.getInstance().getUserInfo(this, userName!!, {
            HeadImgUtils.loadHead(headImg, it.userAvatarURL)
            HeadImgUtils.loadHead(backImg, it.cardBg)
            userNameTv.text = it.userNickname
            userIdTv.text = it.userName
            userIntroTv.text = it.userIntro
            userNoTv.text = "No.${it.userNo}"
            locateTv.text = it.userCity
            coinTv.text = it.userPoint.toString()
            Glide.with(this).load(UserInfoUtils.getUserRoleImg(it.userRole)).into( userRoleImg)
            Glide.with(this).load(if(it.userAppRole=="0")R.mipmap.role_hacker else R.mipmap.role_painter).into( appRoleImg)
            setMedal(it.sysMetal)
        }, { result ->
            toast(result)
        })
    }


    fun setMedal(medalString: String){
        var medal = Gson().fromJson(medalString,Medal::class.java)
        var adapter = MedalAdapter()
        medalRv.adapter = adapter
        adapter.setNewInstance(medal.list as MutableList<MedalItem>)
    }

    class MedalAdapter: BaseQuickAdapter<MedalItem, BaseViewHolder>(R.layout.item_user_medal) {
        override fun convert(holder: BaseViewHolder, item: MedalItem) {
            var attrs = item.attr!!.split("&")
            var img = holder.getView<CircleImageView>(R.id.medalImg)
            Glide.with(context).load(attrs[0].split("=")[1]).into(img)
            holder.setText(R.id.medalTv,item.name)
            holder.setTextColor(R.id.medalTv,Color.parseColor("#"+attrs[2].split("=")[1]))
            var card = holder.getView<CardView>(R.id.medalCard)
            card.setCardBackgroundColor(Color.parseColor("#"+attrs[1].split("=")[1]))
        }
    }



}