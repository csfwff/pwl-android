package com.xiamo.pwl.plugin


import android.view.View
import com.xiamo.pwl.common.PwlEvent
import io.noties.markwon.LinkResolver
import org.greenrobot.eventbus.EventBus

class ImageLinkResolver(val original: LinkResolver) : LinkResolver {
    override fun resolve(view: View, link: String) {
        // decide if you want to open gallery or anything else,
        //  here we just pass to original
//        if (false) {
//            // do your thing
//        } else {
//            // just use original
//            original.resolve(view, link)
//        }
        //Log.e("------",link)
        EventBus.getDefault().post(PwlEvent(1,link))
    }
}