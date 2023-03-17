package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.TransactionStatusesData

interface StatusesCallback: AndroidPOSIntegrationCallback {
    fun onResult(data : TransactionStatusesData)

}