package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.LastReceiptResultData

interface ReceiptCallback {

    /**
     * returns the last issued receipt as an array of strings, one string per line of the receipt.
     */
    fun onResult(data : LastReceiptResultData)

    /**
     * If an error occurs, this method returns the error code.
     */
    fun onError (error : ErrorCode)

    fun onCrash ()
}