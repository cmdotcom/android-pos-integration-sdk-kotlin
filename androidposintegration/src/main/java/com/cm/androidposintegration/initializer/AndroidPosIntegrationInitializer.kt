package com.cm.androidposintegration.initializer

import android.content.Context
import com.cm.androidposintegration.service.PosIntegrationServiceImpl

class AndroidPosIntegrationInitializer {

    private var service: PosIntegrationServiceImpl? = null

    fun create(context: Context): PosIntegrationServiceImpl? {
        service = PosIntegrationServiceImpl(context)
        return service
    }

    fun getInstance(): PosIntegrationServiceImpl? {
        return service
    }
}