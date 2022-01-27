package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.TransactionStatusesData

interface StatusesCallback {
    fun onResult(data : TransactionStatusesData)

    fun onError (error : ErrorCode)

    fun onCrash ()
}