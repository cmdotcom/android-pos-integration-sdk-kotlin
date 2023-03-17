package com.cm.androidposintegration.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.cm.androidposintegration.activity.IntegrationActivity
import com.cm.androidposintegration.beans.*
import com.cm.androidposintegration.enums.TransactionType
import com.cm.androidposintegration.intent.IntentHelper
import com.cm.androidposintegration.intent.IntentHelper.MAX_ORDER_REF_LENGTH
import com.cm.androidposintegration.service.beans.OperationResultBroadcastReceiver
import com.cm.androidposintegration.service.callback.*
import com.cm.androidposintegration.service.callback.beans.ErrorCode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PosIntegrationServiceImpl(private var contextIntegration: Context) : PosIntegrationService {
    private var TAG = PosIntegrationServiceImpl::class.java.simpleName

    // private var contextIntegration: Context? = null
    private var internalReceiver: OperationResultBroadcastReceiver? = null
    private var lastOrderRef: String? = null

    private var operationInProgress = false

    /**
     * Creates a new receiver and registers it on the context given
     */
    private fun registerReceiver(): Boolean {

        internalReceiver = OperationResultBroadcastReceiver()
        contextIntegration.registerReceiver(
            internalReceiver,
            IntentFilter(IntentHelper.INTEGRATION_BROADCAST_INTENT)
        )
        return true

    }

    /**
     * Unregisters the current receiver from the context and deletes it.
     */
    fun unregisterReceiver(): Boolean {

        try {
            contextIntegration.unregisterReceiver(internalReceiver)
        } catch (e: Exception) {
            Log.w(
                TAG, "POS Integration library has recovered from and error " +
                        "while unregistering the receiver"
            )

        }
        internalReceiver = null
        operationInProgress = false
        Log.w("", "unregisterReceiver - operationInProgress $operationInProgress")
        return true

    }

    /**
     * Send an internal intent to start the appropriate operation in PayPlaza side
     * @param data is the data for the operation that needs to be sent.
     * @return true if the intent could be sent
     *          false otherwise.
     */
    private fun sendIntentForOperation(data: Bundle): Boolean {
        // Make sure that the context is not null in the library
        // so everything can be done safely

        val intent = Intent(contextIntegration, IntegrationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtras(data)

        // send the intent to start the operation in PayPlaza apps
        contextIntegration.startActivity(intent)
        operationInProgress = true
        Log.w("","sendIntentForOperation - operationInProgress $operationInProgress")
        return true

    }

    private fun operationInProgressError(callback: AndroidPOSIntegrationCallback) {
        callback.onError(ErrorCode.REPEATED_OPERATION)
    }

    /**
     * Gets the payment data received, creates the internal intent and launches the
     * integrationActivity to send the intent request to payplaza apps
     * @param data payment data to perform the transaction
     * @param callback transaction callback that is going to be called back when
     *                  the operation is done
     */
    override fun doTransaction(data: TransactionData, callback: TransactionCallback) {

        Log.w("", "DoTransaction - operationInProgress $operationInProgress")
        if (operationInProgress) {
            operationInProgressError(callback)
            return
        }

        // Create the intent data for the transaction
        val intentData = Bundle()

        intentData.putString(
            IntentHelper.EXTRA_INTERNAL_INTENT_TYPE,
            IntentHelper.EXTRA_INFORMATION_VALUE_TRANSACTION
        )
        Log.d(TAG, "Payment data object recieved $data")

        // Adding the mandatory extras for the intent payment
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_FLOW, IntentHelper.FLOW_PAYMENT)
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_TYPE, data.type.value)
        intentData.putSerializable(IntentHelper.EXTRA_AMOUNT, data.amount)
        intentData.putString(IntentHelper.EXTRA_CURRENCY_CODE, data.currency.currencyCode)
        if (data.orderReference.length > MAX_ORDER_REF_LENGTH) {
            callback.onError(ErrorCode.MERCHANT_ORDER_REF_TOO_LONG)
            return
        }
        intentData.putString(IntentHelper.EXTRA_ORD_REF, data.orderReference)

        // Adding optional information for the intent payment
        intentData.putBoolean(IntentHelper.EXTRA_CAPTURE_SIGNATURE, data.isCaptureSignature)
        intentData.putBoolean(IntentHelper.EXTRA_SHOW_RECEIPT, data.isShowReceipt)
        intentData.putBoolean(IntentHelper.EXTRA_USE_PROC_STYLE_PROTOCOL, true)
        if (data.language != null) {
            intentData.putString(IntentHelper.EXTRA_LANGUAGE, data.language)
        }
        if (data.type == TransactionType.REFUND) {

            data.refundStan?.let { stan ->
                data.refundDate?.let { date ->
                    val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    val transactionDate = dateFormat.format(date)
                    intentData.putString(IntentHelper.EXTRA_STAN, stan)
                    intentData.putString(IntentHelper.EXTRA_TRANSACTION_DATE, transactionDate)
                    Log.d(TAG, "Date to payplaza apps: ${transactionDate}")
                }
            }
        }

        lastOrderRef = data.orderReference

        // Register the receiver for when the result is back from PayPlaza side
        registerReceiver()

        // Set the callback to send the result to the caller
        internalReceiver?.let {
            it.txCallback = callback
        }

        // Set the service instance that is handling the transaction
        internalReceiver?.let {
            it.integrationServiceImpl = this
        }

        // Send intent to start the transaction in Payplaza Side
        if (!sendIntentForOperation(intentData)) {
            Log.e(TAG, "Cannot send the operation")
            callback.onCrash()
            return
        }
    }

    /**
     * Gets the statuses data received, creates the internal intent and launches the
     * integrationActivity to send the intent request to payplaza apps
     * @param data statuses data to perform the operation
     * @param callback statuses callback that is going to be called back when
     *                  the operation is done
     */
    override fun transactionStatuses(data: RequestStatusData, callback: StatusesCallback) {

        if (operationInProgress) {
            operationInProgressError(callback)
            return
        }

        // Create the intent for the request
        val intentData = Bundle()
        intentData.putString(
            IntentHelper.EXTRA_INTERNAL_INTENT_TYPE,
            IntentHelper.EXTRA_INFORMATION_VALUE_STATUSES
        )

        // Adding the mandatory extras for the intent payment
        if (data.orderReference != null && data.orderReference!!.length > 0) {
            intentData.putString(IntentHelper.EXTRA_ORD_REF, data.orderReference)

        } else {
            intentData.putInt(IntentHelper.EXTRA_PAGE, data.page)
            intentData.putInt(IntentHelper.EXTRA_SIZE, data.size)
            intentData.putString(IntentHelper.EXTRA_SORT_FIELD, data.sortField)
            intentData.putString(IntentHelper.EXTRA_SORT_VALUE, data.sortValue)

        }

        // Register the receiver for when the result is back from PayPlaza side
        registerReceiver()

        // Set the callback to send the result to the caller
        internalReceiver?.let {
            it.statusesCallback = callback

        }

        // Set the service instance that is handling the request
        internalReceiver?.let {
            it.integrationServiceImpl = this

        }

        // Send intent to start the transaction in Payplaza Side
        if (!sendIntentForOperation(intentData)) {
            callback.onCrash()
            return

        }
    }

    /**
     * Gets the Last receipt options received, creates the internal intent and launches the
     * integrationActivity to send the intent request to payplaza apps
     * @param options options of the request to perform the operation
     * @param callback Receipt callback that is going to be called back when
     *                  the operation is done
     */
    override fun getLastReceipt(options: LastReceiptOptions, callback: ReceiptCallback) {

        if (operationInProgress) {
            operationInProgressError(callback)
            return
        }

        // Create the intent data for the request
        val intentData = Bundle()
        intentData.putString(
            IntentHelper.EXTRA_INTERNAL_INTENT_TYPE,
            IntentHelper.EXTRA_INFORMATION_VALUE_RECEIPT
        )
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_FLOW, IntentHelper.FLOW_RECEIPT)
        intentData.putBoolean(IntentHelper.EXTRA_SHOW_RECEIPT, options.isShowReceipt)
        intentData.putBoolean(IntentHelper.EXTRA_USE_PROC_STYLE_PROTOCOL, true)

        // Register the receiver for when the result is back from PayPlaza side
        registerReceiver()

        // Set the callback to send the result to the caller
        internalReceiver?.let {
            it.receiptCallback = callback

        }

        // Set the service instance that is handling the request
        internalReceiver?.let {
            it.integrationServiceImpl = this

        }

        // Send intent to start the operation in Payplaza Side
        if (!sendIntentForOperation(intentData)) {
            callback.onCrash()
            return

        }

    }

    /**
     * Gets the Day Totals options received, creates the internal intent and launches the
     * integrationActivity to send the intent request to payplaza apps
     * @param options options to perform the transaction
     * @param callback Receipt callback that is going to be called back when
     *                  the operation is done
     */
    override fun getTerminalDayTotals(
        options: DayTotalsOptions,
        callback: ReceiptCallback
    ) {

        if (operationInProgress) {
            operationInProgressError(callback)
            return
        }

        // Create the intent data for the request
        val intentData = Bundle()
        intentData.putString(
            IntentHelper.EXTRA_INTERNAL_INTENT_TYPE,
            IntentHelper.EXTRA_INFORMATION_VALUE_TOTALS
        )
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_FLOW, IntentHelper.FLOW_TOTALS)
        intentData.putBoolean(IntentHelper.EXTRA_SHOW_RECEIPT, options.isShowReceipt)
        intentData.putBoolean(IntentHelper.EXTRA_USE_PROC_STYLE_PROTOCOL, true)
        intentData.putString(IntentHelper.EXTRA_DAY_TOTALS_FROM, options.from)

        // Register the receiver for when the result is back from PayPlaza side
        registerReceiver()

        // Set the callback to send the result to the caller
        internalReceiver?.let {
            it.totalsCallback = callback

        }

        // Set the service instance that is handling the request
        internalReceiver?.let {
            it.integrationServiceImpl = this

        }

        // Send intent to start the operation in Payplaza Side
        if (!sendIntentForOperation(intentData)) {
            callback.onCrash()
            return

        }

    }

    /**
     * Gets the info exposed by the terminal app in the get info request intent
     * @param callback is the callback object to send the result to the caller
     */
    override fun getTerminalInfo(callback: TerminalInfoCallback) {

        if (operationInProgress) {
            operationInProgressError(callback)
            return
        }

        // Intent data for the request to PayPlaza side
        val intentData = Bundle()
        intentData.putString(
            IntentHelper.EXTRA_INTERNAL_INTENT_TYPE,
            IntentHelper.EXTRA_INFORMATION_VALUE_INFO
        )

        // Register the receiver for when the result is back from PayPlaza side
        registerReceiver()

        // Set the callback to send the result to the caller
        internalReceiver?.let {
            it.infoCallback = callback

        }

        // Set the service instance that is handling the request
        internalReceiver?.let {
            it.integrationServiceImpl = this

        }

        // Send intent to start the operation in Payplaza Side
        if (!sendIntentForOperation(intentData)) {
            callback.onCrash()
            return

        }

    }

    override fun finishPreAuth(data: PreAuthFinishData, callback: TransactionCallback) {

        if (operationInProgress) {
            operationInProgressError(callback)
            return
        }

        // Create the intent data for the transaction
        val intentData = Bundle()

        intentData.putString(
            IntentHelper.EXTRA_INTERNAL_INTENT_TYPE,
            IntentHelper.EXTRA_INFORMATION_VALUE_TRANSACTION
        )
        Log.d(TAG, "Payment data object recieved $data")

        // Adding the mandatory extras for the intent payment
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_FLOW, IntentHelper.FLOW_PAYMENT)
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_TYPE, data.type.value)
        data.amount?.let {
            intentData.putSerializable(IntentHelper.EXTRA_AMOUNT, it)
        }

        data.currency?.let {
            intentData.putString(IntentHelper.EXTRA_CURRENCY_CODE, it.currencyCode)
        }


        if (data.orderRef.length > MAX_ORDER_REF_LENGTH) {
            Log.d(TAG, "Order ref too long")
            callback.onError(ErrorCode.MERCHANT_ORDER_REF_TOO_LONG)
            return

        }

        intentData.putString(IntentHelper.EXTRA_ORD_REF, data.orderRef)

        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val transactionDate = dateFormat.format(data.originalDate)
        intentData.putString(IntentHelper.EXTRA_STAN, data.originalStan)
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_DATE, transactionDate)
        intentData.putBoolean(IntentHelper.EXTRA_SHOW_RECEIPT, data.isShowReceipt)
        intentData.putBoolean(IntentHelper.EXTRA_USE_PROC_STYLE_PROTOCOL, true)
        Log.d(TAG, "Date to payplaza apps: ${transactionDate}")

        lastOrderRef = data.orderRef

        // Register the receiver for when the result is back from PayPlaza side
        registerReceiver()

        // Set the callback to send the result to the caller
        internalReceiver?.let {
            it.txCallback = callback

        }

        // Set the service instance that is handling the transaction
        internalReceiver?.let {
            it.integrationServiceImpl = this

        }

        // Send intent to start the transaction in Payplaza Side
        if (!sendIntentForOperation(intentData)) {
            callback.onCrash()
            return

        }
    }

}