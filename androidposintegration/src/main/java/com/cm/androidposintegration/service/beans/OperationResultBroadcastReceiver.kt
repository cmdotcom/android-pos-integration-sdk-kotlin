package com.cm.androidposintegration.service.beans

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import com.cm.androidposintegration.beans.ReceiptData
import com.cm.androidposintegration.enums.TransactionResult
import com.cm.androidposintegration.intent.IntentHelper
import com.cm.androidposintegration.intent.IntentUtil
import com.cm.androidposintegration.service.PosIntegrationServiceImpl
import com.cm.androidposintegration.service.callback.ReceiptCallback
import com.cm.androidposintegration.service.callback.StatusesCallback
import com.cm.androidposintegration.service.callback.TerminalInfoCallback
import com.cm.androidposintegration.service.callback.TransactionCallback
import com.cm.androidposintegration.service.callback.beans.*
import com.cm.androidposintegration.service.callback.json.StatusResponseParser
import java.lang.IllegalArgumentException
import java.util.*

class OperationResultBroadcastReceiver: BroadcastReceiver() {
    private var TAG = OperationResultBroadcastReceiver::class.java.simpleName

    var integrationServiceImpl: PosIntegrationServiceImpl? = null
    var txCallback : TransactionCallback? = null
    var statusesCallback : StatusesCallback? = null
    var receiptCallback : ReceiptCallback? = null
    var totalsCallback : ReceiptCallback? = null
    var infoCallback: TerminalInfoCallback? = null


    /**
     * gets the error code received from Payplaza apps.
     * @param data intent received as a response from payplaza apps
     */
    private fun getErrorCode(data: Intent?): Int {
        if (data != null) {
            return data.getIntExtra(IntentHelper.EXTRA_ERROR_CODE, ErrorCode.UNKNOWN_ERROR.value)

        }

        return ErrorCode.UNKNOWN_ERROR.value

    }

    private fun getStringExtraIfNotNull (data: Intent, extraName: String): String? {
        if (data.hasExtra(extraName)) {
            return data.getStringExtra(extraName)

        }

        return null
    }

    /**
     * Converts the Transaction result received from payplaza apps to the appropriate TransactionResult
     * @param data intent received as result form payplaza apps
     */
    private fun matchTransactionResult(data: Intent?, extra2Watch: String): TransactionResult {
        if (data != null) {
            if (data.hasExtra(extra2Watch)) {
                try {
                    return TransactionResult.valueOf(data.getStringExtra(extra2Watch)!!.uppercase())

                } catch (iae: IllegalArgumentException) {
                    Log.e(TAG,"Type received not recognised", iae)
                    return TransactionResult.FAILED

                }
            }
        }

        return TransactionResult.CANCELED
    }

    /**
     * Gets the receipt information from the intent response. It looks the merchant receipt
     * or the customer receipt based on parameter
     * @param data intent received as a response to a one request to the payplaza apps
     * @param isMerchantReceipt if true, function is going to look for merchant receipt. Otherwise
     * is going to look for customer receipt.
     */
    private fun getReceiptFromIntent(data: Intent, isMerchantReceipt: Boolean): ReceiptData? {
        val receiptToGet: String = if (isMerchantReceipt) IntentHelper.EXTRA_MERCHANT_RECEIPT else IntentHelper.EXTRA_CUSTOMER_RECEIPT
        val signatureToGet: String = if (isMerchantReceipt) IntentHelper.EXTRA_MERCHANT_SIGNATURE else IntentHelper.EXTRA_CUSTOMER_SIGNATURE

        if (data.hasExtra(receiptToGet)) {
            val receiptData = ReceiptData()
            if (data.hasExtra(signatureToGet)) {
                val rawSignature = data.getByteArrayExtra(signatureToGet)
                if (rawSignature != null) {
                    receiptData.signature = rawSignature

                }
            }

            val lines = data.getStringArrayExtra(receiptToGet)
            receiptData.receiptLines = lines
            return receiptData

        }

        // If there is no data or not receipt extra in the response, then return a null object.
        return null
    }

    private fun addAmountFields(resultData: TransactionResultData, intent: Intent) {
        resultData.apply {
            amount = if (IntentUtil.getBigDecimal(intent, IntentHelper.EXTRA_AMOUNT).isPresent) {
                IntentUtil.getBigDecimal(intent, IntentHelper.EXTRA_AMOUNT).get()
            } else {
                null
            }

        }

        resultData.apply {
            tipAmount = if(IntentUtil.getBigDecimal(intent, IntentHelper.EXTRA_TIP_AMOUNT).isPresent) {
                IntentUtil.getBigDecimal(intent, IntentHelper.EXTRA_TIP_AMOUNT).get()
            } else {
                null
            }

        }

    }

    /**
     * Adds extra information to a TransactionResultData object.
     * Get that information from the intent that has been received as a result
     * to a Transaction operation.
     * @param resultData object in which the extra information is going to be stored
     * @param intent intent in which the information is contained as extras
     */
    private fun addExtraFields(resultData: TransactionResultData, intent: Intent) {
        resultData.apply {
            authResponseCode = intent.getStringExtra(IntentHelper.EXTRA_AUTH_RESPONSE_CODE)
        }

        resultData.apply {
            cardEntryMode = intent.getStringExtra(IntentHelper.EXTRA_CARD_ENTRY_MODE)

        }

        resultData.apply {
            ecrId = intent.getStringExtra(IntentHelper.EXTRA_ECR_ID)

        }

        resultData.apply {
            processorName = intent.getStringExtra(IntentHelper.EXTRA_PROCESSOR_NAME)

        }

        resultData.apply {
            transactionDateTime = if (IntentUtil.getDate(intent, IntentHelper.EXTRA_TRANSACTION_DATE_TIME).isPresent ) {
                IntentUtil.getDate(intent, IntentHelper.EXTRA_TRANSACTION_DATE_TIME).get()

            } else {
                null

            }
        }

        resultData.apply {
            transactionId = intent.getStringExtra(IntentHelper.EXTRA_TRANSACTION_ID)

        }

        resultData.apply {
            cardScheme = intent.getStringExtra(IntentHelper.EXTRA_CARD_SCHEME)

        }

        resultData.apply {
            aid = intent.getStringExtra(IntentHelper.EXTRA_AID)

        }

        resultData.apply {
            cardNumber = intent.getStringExtra(IntentHelper.EXTRA_CARD_NUMBER)
        }

        resultData.apply {
            cardType = intent.getIntExtra(IntentHelper.EXTRA_CARD_TYPE, 0)

        }

        resultData.apply {
            stan = intent.getStringExtra(IntentHelper.EXTRA_STAN)

        }

    }

    /**
     * Gets the information received on the intent result for a transaction request.
     * Process all information that may be received on the response
     * and creates the apropriate TransactionResultData object that is going to be
     * used in the TransactionCallback for the transaction operation.
     * If error occurred during the operation, the appropriate onError and onCrash methods
     * are called on the callback too.
     * @see com.payplaza.androidposintegration.service.callback.beans.TransactionResultData
     * @see com.payplaza.androidposintegration.service.callback.TransactionCallback
     * @param operationResult result of the intent received on library from payplaza apps
     * @param data intent received as result from payplaza apps
     */
    private fun processResultFromTransaction(operationResult: Int, data: Intent) {
        if ((operationResult != Activity.RESULT_OK && operationResult != Activity.RESULT_CANCELED) || !data.hasExtra(
                IntentHelper.EXTRA_ORD_REF)) {
            Log.d(TAG, "Activity result not ok or no Order ref $operationResult ${data.hasExtra(
                IntentHelper.EXTRA_ORD_REF)}")
            txCallback?.onCrash()

        } else {
            val erroCode = getErrorCode(data)
            if (erroCode == ErrorCode.NO_ERROR.value) {
                Log.d(TAG,"No_Error error code in result")
                val txResult = matchTransactionResult(data, IntentHelper.EXTRA_TRANSACTION_RESULT)
                val orderRef = data.getStringExtra(IntentHelper.EXTRA_ORD_REF)
                val resultData = TransactionResultData(txResult, orderRef!!)
                addAmountFields(resultData, data)
                addExtraFields(resultData, data)
                resultData.apply {
                    merchantReceipt = getReceiptFromIntent(data, true)

                }

                resultData.apply {
                    customerReceipt = getReceiptFromIntent(data, false)

                }

                txCallback?.onResult(resultData)

            } else {
                val errorData = ErrorCode.getByValue(erroCode)
                if (errorData != null) {
                    Log.d(TAG, "Valid error code in transaction result")
                    txCallback?.onError(errorData)

                } else {
                    Log.d(TAG, "No error or unknown error code in transaction result ${erroCode}")
                    txCallback?.onCrash()

                }

            }
        }
    }

    /**
     * Gets the information received on the intent result for a print last receipt request.
     * Process all information that may be received on the response
     * and creates the apropriate LastReceiptResultData object that is going to be
     * used in the ReceiptCallback for the print last receipt operation.
     * If error occurred during the operation, the appropriate onError and onCrash methods
     * are called on the callback too.
     * @see com.payplaza.androidposintegration.service.callback.beans.LastReceiptResultData
     * @see com.payplaza.androidposintegration.service.callback.ReceiptCallback
     * @param operationResult result of the intent received on library from payplaza apps
     * @param data intent received as result from payplaza apps
     */
    private fun processResultFromGetReceipt(operationResult: Int, data: Intent) {
        if (operationResult != Activity.RESULT_OK) {
            receiptCallback!!.onCrash()

        } else {
            val errorCode = getErrorCode(data)
            if (errorCode == ErrorCode.NO_ERROR.value) {
                val receipt = getReceiptFromIntent(data, false)
                val receiptResultData = LastReceiptResultData(receipt)
                receiptCallback!!.onResult(receiptResultData)

            } else {
                val errorData = ErrorCode.getByValue(errorCode)
                if (errorData != null) {
                    receiptCallback?.onError(errorData)

                } else {
                    receiptCallback?.onCrash()

                }

            }
        }
    }

    /**
     * Gets the information received on the intent result for a totals request.
     * Process all information that may be received on the response
     * and creates the apropriate LastReceiptResultData object that is going to be
     * used in the ReceiptCallback for the totals operation.
     * If error occurred during the operation, the appropriate onError and onCrash methods
     * are called on the callback too.
     * @see com.payplaza.androidposintegration.service.callback.beans.LastReceiptResultData
     * @see com.payplaza.androidposintegration.service.callback.ReceiptCallback
     * @param operationResult result of the intent received on library from payplaza apps
     * @param data intent received as result from payplaza apps
     */
    private fun processResultFromTotals(operationResult: Int, data: Intent) {
        if (operationResult != Activity.RESULT_OK) {
            totalsCallback!!.onCrash()
        } else {
            val errorCode = getErrorCode(data)
            if (errorCode == ErrorCode.NO_ERROR.value) {
                var receipt = getReceiptFromIntent(data, true)
                if (receipt == null) {
                    receipt = getReceiptFromIntent(data, false)
                }
                val receiptResultData = LastReceiptResultData(receipt)
                totalsCallback?.onResult(receiptResultData)


            } else {
                val errorData = ErrorCode.getByValue(errorCode)
                if (errorData != null) {
                    totalsCallback?.onError(errorData)
                } else {
                    totalsCallback?.onCrash()
                }
            }
        }
    }

    /**
     * Gets the information received on the intent result for a
     * statuses request. Process all information that may be received on the response
     * and creates the apropriate TransactionStatusesData object that is going to be
     * used in the StatusesCallback.
     * If error occurred during the operation, the appropriate onError and onCrash methods
     * are called on the callback too.
     * @see com.payplaza.androidposintegration.service.callback.beans.TransactionStatusesData
     * @see com.payplaza.androidposintegration.service.callback.StatusesCallback
     *
     */
    private fun processResultFromGetTxStatuses(operationResult: Int, data: Intent) {
        if (operationResult != Activity.RESULT_OK) {
            statusesCallback!!.onCrash()
        } else {
            // Check if the rest of the information is present
            var statusJson: String? = null
            if (data.hasExtra(IntentHelper.EXTRA_TRANSACTION_STATUS_DATA)) {
                statusJson = String(Base64.decode(data.getStringExtra(IntentHelper.EXTRA_TRANSACTION_STATUS_DATA), 0))

            }

            var statusErrorMessage: String? = null
            if (data.hasExtra(IntentHelper.EXTRA_TRANSACTION_STATUS_ERROR)) {
                statusErrorMessage = data.getStringExtra(IntentHelper.EXTRA_TRANSACTION_STATUS_ERROR)

            }

            val totalCount = data.getIntExtra(IntentHelper.EXTRA_TRANSACTION_STATUS_TOTAL_COUNT, 0)
            if (statusJson != null) {
                statusErrorMessage = (if (statusErrorMessage == null){"No error"} else {statusErrorMessage})
                statusesCallback!!.onResult(
                    TransactionStatusesData(
                        StatusResponseParser.getTxStatuses(statusJson), statusErrorMessage, totalCount
                    )
                )

            } else {
                statusesCallback!!.onError(ErrorCode.TRANSACTION_STATUS_ERROR)

            }
        }
    }

    private fun getInfoFromInfoIntent(infoResult: TerminalInfoData, data: Intent) {
        infoResult.apply {
            storeName = getStringExtraIfNotNull(data, IntentHelper.EXTRA_STORE_NAME)

        }

        infoResult.apply {
            storeAddress = getStringExtraIfNotNull(data, IntentHelper.EXTRA_STORE_ADDRESS)

        }

        infoResult.apply {
            storeCity = getStringExtraIfNotNull(data, IntentHelper.EXTRA_STORE_CITY)

        }

        infoResult.apply {
            storeZipCode = getStringExtraIfNotNull(data, IntentHelper.EXTRA_STORE_ZIP_CODE)

        }

        infoResult.apply {
            storeLanguage = getStringExtraIfNotNull(data, IntentHelper.EXTRA_STORE_LANGUAGE)

        }

        infoResult.apply {
            storeCountry = getStringExtraIfNotNull(data, IntentHelper.EXTRA_STORE_COUNTRY)

        }

        val currencySimbol = getStringExtraIfNotNull(data, IntentHelper.EXTRA_STORE_CURRENCY)
        if (currencySimbol != null) {
            infoResult.apply {
                storeCurrency = Currency.getInstance(currencySimbol)

            }
        }

        infoResult.apply {
            deviceSerialNumber = getStringExtraIfNotNull(data, IntentHelper.EXTRA_DEVICE_SERIAL)

        }

        infoResult.apply {
            versionNumber = getStringExtraIfNotNull(data, IntentHelper.EXTRA_TERMINAL_VERSION_NUMBER)

        }

    }

    private fun processResultFromInfoRequest(operationResult: Int, data: Intent) {
        if (operationResult != Activity.RESULT_OK) {
            infoCallback!!.onCrash()

        } else {
            val infoResult = TerminalInfoData(matchTransactionResult(data, IntentHelper.EXTRA_INFO_RESULT))
            if (infoResult.transactionResult == TransactionResult.FAILED) {
                Log.d(TAG, "Transaction failed")
                infoCallback?.onError(ErrorCode.INFO_REQUEST_FAILED)

            } else {
                getInfoFromInfoIntent(infoResult, data)

                Log.d(TAG, "Received info from Payplaza apps ${infoResult}")
                infoCallback?.onResult(infoResult)

            }


        }
    }

    /**
     * Process the result based on the request that has been sent
     * (Transaction, Receipt, statuses or totals)
     */
    private fun processResultBasedOnRequest(intent: Intent) {
        val typeBroadcast = intent.getStringExtra(IntentHelper.EXTRA_INFORMATION_RECEIVED_TYPE)
        val operationResult = intent.getIntExtra(IntentHelper.EXTRA_INTERNAL_OPERATION_RESULT, Activity.RESULT_CANCELED)
        when (typeBroadcast) {
            IntentHelper.EXTRA_INFORMATION_VALUE_TRANSACTION -> {
                Log.d(TAG,"Received information about a transaction")
                processResultFromTransaction(operationResult, intent)

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_RECEIPT -> {
                processResultFromGetReceipt(operationResult, intent)

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_STATUSES -> {
                processResultFromGetTxStatuses(operationResult, intent)

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_TOTALS -> {
                processResultFromTotals(operationResult, intent)

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_INFO -> {
                processResultFromInfoRequest(operationResult, intent)

            }
        }
    }

    override fun onReceive(mContext: Context?, data: Intent?) {
        Log.d(TAG, "Received internal broadcast with the response data $data")

        if (data != null) {
            Log.d(TAG,"Data is not null")

            if (!data.hasExtra(IntentHelper.EXTRA_INFORMATION_RECEIVED_TYPE)) {
                Log.e(TAG, "Data has no information about received type")
                return

            }

            processResultBasedOnRequest(data)

            integrationServiceImpl?.unregisterReceiver()
        }
    }
}