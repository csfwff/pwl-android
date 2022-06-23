package com.xiamo.pwl.plugin

import android.util.Log
import org.commonmark.node.Image
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.image.ImageProps

class ImageClickPlugin: AbstractMarkwonPlugin() {

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.appendFactory(Image::class.java){ configuration,props->
            var url = ImageProps.DESTINATION.require(props)
            LinkSpan(
                configuration.theme(),
                url,
                ImageLinkResolver(configuration.linkResolver())
            )
        }
    }


}