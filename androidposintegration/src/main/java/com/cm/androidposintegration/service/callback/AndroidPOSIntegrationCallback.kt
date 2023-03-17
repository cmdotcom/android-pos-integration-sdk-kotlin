package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode

interface AndroidPOSIntegrationCallback {

    fun onError (error : ErrorCode)

    fun onCrash ()
}