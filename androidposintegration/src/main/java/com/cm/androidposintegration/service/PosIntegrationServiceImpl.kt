package com.cm.androidposintegration.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.cm.androidposintegration.IntegrationActivity
import com.cm.androidposintegration.beans.DayTotalsOptions
import com.cm.androidposintegration.beans.LastReceiptOptions
import com.cm.androidposintegration.beans.RequestStatusData
import com.cm.androidposintegration.beans.TransactionData
import com.cm.androidposintegration.enums.TransactionType
import com.cm.androidposintegration.intent.IntentHelper
import com.cm.androidposintegration.intent.IntentHelper.MAX_ORDER_REF_LENGTH
import com.cm.androidposintegration.service.beans.OperationResultBroadcastReceiver
import com.cm.androidposintegration.service.callback.ReceiptCallback
import com.cm.androidposintegration.service.callback.StatusesCallback
import com.cm.androidposintegration.service.callback.TerminalInfoCallback
import com.cm.androidposintegration.service.callback.TransactionCallback
import com.cm.androidposintegration.service.callback.beans.ErrorCode
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PosIntegrationServiceImpl: PosIntegrationService {
    private var TAG = PosIntegrationServiceImpl::class.java.simpleName
    private var contextIntegration: Context? = null
    private var internalReceiver: OperationResultBroadcastReceiver? = null
    private var lastOrderRef: String? = null

    fun setContext(context: Context) {
        contextIntegration = context
    }

    /**
     * Creates a new receiver and registers it on the context given
     */
    private fun registerReceiver(): Boolean {
        contextIntegration?.let {
            internalReceiver = OperationResultBroadcastReceiver()
            it.registerReceiver(internalReceiver, IntentFilter(IntentHelper.INTEGRATION_BROADCAST_INTENT))
            return true
        }

        return false

    }

    /**
     * Unregisters the current receiver from the context and deletes it.
     */
    fun unregisterReceiver(): Boolean {
        contextIntegration?.let {
            it.unregisterReceiver(internalReceiver)
            internalReceiver = null
            return true
        }

        return false
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
        contextIntegration?.let {
            val intent = Intent(contextIntegration, IntegrationActivity::class.java)
            intent.putExtras(data)

            // send the intent to start the operation in PayPlaza apps
            it.startActivity(intent)
            return true
        }

        return false
    }

    /**
     * Gets the payment data received, creates the internal intent and launches the
     * integrationActivity to send the intent request to payplaza apps
     * @param data payment data to perform the transaction
     * @param callback transaction callback that is going to be called back when
     *                  the operation is done
     */
    override fun doTransaction(data: TransactionData, callback: TransactionCallback) {
        // Create the intent data for the transaction
        val intentData = Bundle()

        intentData.putString(IntentHelper.EXTRA_INTERNAL_INTENT_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_TRANSACTION)
        Log.d(TAG, "Payment data object recieved $data")

        // Adding the mandatory extras for the intent payment
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_FLOW, IntentHelper.FLOW_PAYMENT)
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_TYPE, data.type.value)
        intentData.putSerializable(IntentHelper.EXTRA_AMOUNT, data.amount)
        intentData.putString(IntentHelper.EXTRA_CURRENCY_CODE, data.currency.currencyCode)
        if (data.orderReference.length > MAX_ORDER_REF_LENGTH){
            callback.onError(ErrorCode.MERCHANT_ORDER_REF_TOO_LONG)
            return

        }
        intentData.putString(IntentHelper.EXTRA_ORD_REF, data.orderReference)

        // Adding optional information for the intent payment
        intentData.putBoolean(IntentHelper.EXTRA_TIPPING, data.isTipping)
        intentData.putBoolean(IntentHelper.EXTRA_CAPTURE_SIGNATURE, data.isCaptureSignature)
        intentData.putBoolean(IntentHelper.EXTRA_SHOW_RECEIPT, data.isShowReceipt)
        intentData.putBoolean(IntentHelper.EXTRA_USE_PROC_STYLE_PROTOCOL, true)
        if (data.language != null) {
            intentData.putString(IntentHelper.EXTRA_LANGUAGE, data.language)

        }
        if (data.type == TransactionType.REFUND) {

            data.refundStan?.let { stan ->
                data.refundDate?.let {date ->
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
        if (!registerReceiver()) {
            callback.onCrash()
            return

        }

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

    /**
     * Gets the statuses data received, creates the internal intent and launches the
     * integrationActivity to send the intent request to payplaza apps
     * @param data statuses data to perform the operation
     * @param callback statuses callback that is going to be called back when
     *                  the operation is done
     */
    override fun transactionStatuses(data: RequestStatusData, callback: StatusesCallback) {

        // Create the intent for the request
        val intentData = Bundle()
        intentData.putString(IntentHelper.EXTRA_INTERNAL_INTENT_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_STATUSES)

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
        if (!registerReceiver()) {
            callback.onCrash()
            return

        }

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

        // Create the intent data for the request
        val intentData = Bundle()
        intentData.putString(IntentHelper.EXTRA_INTERNAL_INTENT_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_RECEIPT)
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_FLOW, IntentHelper.FLOW_RECEIPT)
        intentData.putBoolean(IntentHelper.EXTRA_SHOW_RECEIPT, options.isShowReceipt)
        intentData.putBoolean(IntentHelper.EXTRA_USE_PROC_STYLE_PROTOCOL, true)

        // Register the receiver for when the result is back from PayPlaza side
        if (!registerReceiver()) {
            callback.onCrash()
            return

        }

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
        // Create the intent data for the request
        val intentData = Bundle()
        intentData.putString(IntentHelper.EXTRA_INTERNAL_INTENT_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_TOTALS)
        intentData.putString(IntentHelper.EXTRA_TRANSACTION_FLOW, IntentHelper.FLOW_TOTALS)
        intentData.putBoolean(IntentHelper.EXTRA_SHOW_RECEIPT, options.isShowReceipt)
        intentData.putBoolean(IntentHelper.EXTRA_USE_PROC_STYLE_PROTOCOL, true)
        intentData.putString(IntentHelper.EXTRA_DAY_TOTALS_FROM, options.from)

        // Register the receiver for when the result is back from PayPlaza side
        if (!registerReceiver()) {
            callback.onCrash()
            return

        }

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
        // Intent data for the request to PayPlaza side
        val intentData = Bundle()
        intentData.putString(IntentHelper.EXTRA_INTERNAL_INTENT_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_INFO)

        // Register the receiver for when the result is back from PayPlaza side
        if (!registerReceiver()) {
            callback.onCrash()
            return

        }

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

}