
package com.dresscode.app

import android.app.Application
import android.content.Context

class DressCodeApp : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
