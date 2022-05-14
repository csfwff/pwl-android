package com.xiamo.pwl.ui


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiamo.pwl.R
import com.xiamo.pwl.bean.EmojiItem
import com.xiamo.pwl.common.BASE_URL
import com.xiamo.pwl.util.HeadImgUtils

class EmojiAdapter: BaseQuickAdapter<EmojiItem, BaseViewHolder>(R.layout.item_emoji)  {

    override fun convert(holder: BaseViewHolder, item: EmojiItem) {
        var url = item.url.replace("https://\${config.domain}", BASE_URL)
        HeadImgUtils.loadHead(holder.getView(R.id.emojiImg),url)
    }
}