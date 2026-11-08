package com.tencent.tmm.kmmresource

import android.content.Context

object KmmResource {
    @Volatile
    private var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    internal val context: Context
        get() = checkNotNull(applicationContext) {
            "Call KmmResource.initialize(context) in Application.onCreate() before reading resources."
        }
}
