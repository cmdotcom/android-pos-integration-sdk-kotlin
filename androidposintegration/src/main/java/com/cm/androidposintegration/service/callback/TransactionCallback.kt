package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.TransactionResultData

interface TransactionCallback {
    fun onResult(data : TransactionResultData)

    fun onError (error : ErrorCode)

    fun onCrash ()
}