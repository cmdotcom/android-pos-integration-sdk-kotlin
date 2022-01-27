package com.cm.androidposintegration.initializer

import android.content.Context
import com.cm.androidposintegration.service.PosIntegrationServiceImpl

class AndroidPosIntegrationInitializer {

    var service: PosIntegrationServiceImpl = PosIntegrationServiceImpl()

    fun create(context: Context): PosIntegrationServiceImpl {

        service.setContext(context)

        return service
    }

    fun getInstance(): PosIntegrationServiceImpl {
        return service
    }
}