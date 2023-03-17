package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.TransactionResultData

interface TransactionCallback: AndroidPOSIntegrationCallback {
    fun onResult(data : TransactionResultData)

}