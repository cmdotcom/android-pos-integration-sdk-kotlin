package com.cm.androidposintegration.beans

import com.cm.androidposintegration.enums.TransactionType
import java.math.BigDecimal
import java.util.*

data class TransactionData(
    val type: TransactionType,
    val amount: BigDecimal,
    val currency: Currency,
    val orderReference: String
) {

    var language: String? = null
    var refundStan: String? = null
    var refundDate: Date? = null
    var isCaptureSignature = true
    var isShowReceipt = true
    var maxOfflineSaleAmount: BigDecimal? = null
    var maxOfflineTransactionsCount: Int? = null
    var maxOfflineSaleAmountPerTransaction: BigDecimal? = null

    override fun toString(): String {
        return "PaymentData { 'TransactionType' : $type, " +
                "'Amount' : $amount, " +
                "'Currency' : ${currency}, " +
                "'Language' : $language, " +
                "'orderRef' : ${orderReference}, " +
                "'refundStan' : ${refundStan}, " +
                "'refundDate' : ${refundDate}, " +
                "'captureSignature' : $isCaptureSignature, " +
                "'isShowReceipt' : $isShowReceipt, " + "" +
                "'maxOfflineSaleAmount' : $maxOfflineSaleAmount, " +
                "'maxOfflineTransactionsCount' : $maxOfflineTransactionsCount, " +
                "'maxOfflineSaleAmountPerTransaction' : $maxOfflineSaleAmountPerTransaction }"
    }

}