package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.LastReceiptResultData

interface ReceiptCallback: AndroidPOSIntegrationCallback {

    /**
     * returns the last issued receipt as an array of strings, one string per line of the receipt.
     */
    fun onResult(data : LastReceiptResultData)
}