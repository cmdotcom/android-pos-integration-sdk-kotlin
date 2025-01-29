package com.cm.androidposintegration.initializer

import android.content.Context
import android.util.Log
import com.cm.androidposintegration.service.PosIntegrationServiceImpl

@Suppress("unused")
object AndroidPosIntegration {

    private val TAG = AndroidPosIntegration::class.java.simpleName
    private var initializer: AndroidPosIntegrationInitializer? = null

    fun init(context: Context) {
        if (initializer == null) {
            Log.d(TAG, "creating initializer")
            initializer = AndroidPosIntegrationInitializer()
        }

        Log.d(TAG, "creating service")
        initializer?.create(context)

    }

    fun getInstance(): PosIntegrationServiceImpl? {
        var service: PosIntegrationServiceImpl? = null
        if (initializer != null) {
            Log.d(TAG, "Initializer not null, geting the instance")
            service = initializer!!.getInstance()

        }

        return service

    }
}