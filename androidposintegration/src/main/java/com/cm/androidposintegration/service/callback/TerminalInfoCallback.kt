package com.cm.androidposintegration.service.callback

import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.TerminalInfoData

interface TerminalInfoCallback {
    fun onResult(data : TerminalInfoData)

    fun onError (error : ErrorCode)

    fun onCrash ()
}