package com.cm.androidposintegration.service

import com.cm.androidposintegration.beans.DayTotalsOptions
import com.cm.androidposintegration.beans.LastReceiptOptions
import com.cm.androidposintegration.beans.RequestStatusData
import com.cm.androidposintegration.beans.TransactionData
import com.cm.androidposintegration.service.callback.ReceiptCallback
import com.cm.androidposintegration.service.callback.StatusesCallback
import com.cm.androidposintegration.service.callback.TerminalInfoCallback
import com.cm.androidposintegration.service.callback.TransactionCallback

interface PosIntegrationService {
    fun doTransaction(data: TransactionData, callback: TransactionCallback)

    fun transactionStatuses(data: RequestStatusData, callback: StatusesCallback)

    fun getLastReceipt(options: LastReceiptOptions, callback: ReceiptCallback)

    fun getTerminalDayTotals(options: DayTotalsOptions, callback: ReceiptCallback)

    fun getTerminalInfo(callback: TerminalInfoCallback)
}