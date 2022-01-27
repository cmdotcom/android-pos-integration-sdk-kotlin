package com.cm.androidposintegration.service.callback.beans

import android.os.Parcelable
import com.cm.androidposintegration.beans.ReceiptData
import com.cm.androidposintegration.enums.TransactionResult
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
data class TransactionResultData (val transactionResult: TransactionResult,
                                  val orderReference: String,
                                  var amount: BigDecimal? = null,
                                  var authResponseCode: String? = null,
                                  var cardEntryMode: String? = null,
                                  var ecrId: String? = null,
                                  var processorName: String? = null,
                                  var transactionDateTime: Date? = null,
                                  var transactionId: String? = null,
                                  var cardScheme: String? = null,
                                  var aid: String? = null,
                                  var cardNumber: String? = null,
                                  var cardType: Int? = null,
                                  var stan: String? = null,
                                  var merchantReceipt: ReceiptData? = null,
                                  var customerReceipt: ReceiptData? = null,
                                  var tipAmount: BigDecimal? = null): Parcelable {

    override fun toString() : String {
        return "TransactionResultData { 'TransactionResult' : $transactionResult, " +
                "'OrderReference' : $orderReference" +
                "'Amount' : $amount, " +
                "'TipAmount' : $tipAmount, " +
                "'Auth Resp Code' : ${authResponseCode}, " +
                "'CardEntryMode' : $cardEntryMode, " +
                "'ecrId' : ${ecrId}, " +
                "'processorName' : ${processorName}, " +
                "'transactionDateTime' : ${transactionDateTime}, " +
                "'transactionId' : ${transactionId}, " +
                "'CardScheme' : ${cardScheme} " +
                "'aid' : ${aid}, " +
                "'cardNumber' : ${cardNumber}, " +
                "'cardType' : ${cardType}, " +
                "'stan' : ${stan} }"
    }

}