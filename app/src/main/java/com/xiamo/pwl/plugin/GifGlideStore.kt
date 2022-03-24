package com.xiamo.pwl.plugin

import android.graphics.drawable.Animatable
import androidx.annotation.NonNull

import android.graphics.drawable.Drawable
import androidx.annotation.Nullable

import com.bumptech.glide.load.engine.GlideException

import com.bumptech.glide.request.RequestListener

import io.noties.markwon.image.AsyncDrawable

import com.bumptech.glide.RequestBuilder

import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target

import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin.GlideStore


internal class GifGlideStore(private val requestManager: RequestManager) : GlideStore {
    override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
        // NB! Strange behaviour: First time a resource is requested - it fails, the next time it loads fine
        val destination = drawable.destination
        return requestManager // it is enough to call this (in order to load GIF and non-GIF)
            .asDrawable()
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is Animatable) {
                        (resource as Animatable).start()
                    }
                    return false
                }
            })
            .load(destination)
    }

    override fun cancel(target: com.bumptech.glide.request.target.Target<*>) {
        requestManager.clear(target);
    }
}