package com.xiamo.pwl.common

import android.app.Application
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class PwlApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY) //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)
        builder.connectTimeout(60000, TimeUnit.MILLISECONDS)
        builder.writeTimeout(60000, TimeUnit.MILLISECONDS)
        builder.readTimeout(60000, TimeUnit.MILLISECONDS)

        OkGo.getInstance().init(this)
            .setOkHttpClient(builder.build())
            .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
            .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
            .setRetryCount(3)
    }

    companion object {
        var instance: PwlApplication? = null
            private set
    }

}