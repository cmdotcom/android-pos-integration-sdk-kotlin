package com.cm.androidposintegration.service.callback.json

import com.cm.androidposintegration.beans.ReceiptData
import com.cm.androidposintegration.enums.TransactionResult
import com.cm.androidposintegration.enums.TransactionType
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.*

object StatusResponseParser {
    fun getTxStatuses (json: String): List<TransactionStatusData> {
        val listFromStringJson : List<TransactionStatusSimpleData> =
            Gson()
                .fromJson(json, Array<TransactionStatusSimpleData>::class.java)
                .toList()
        val resultList = mutableListOf<TransactionStatusData>()
        listFromStringJson.forEach{
            val resultStatus = if ("APPROVED".equals(it.result)) { TransactionResult.SUCCESS } else { TransactionResult.FAILED}
            val amount : BigDecimal
            val currency : Currency
            if (it.amount_of_money != null) {
                amount = it.amount_of_money.value
                currency = Currency.getInstance(it.amount_of_money.currency)

            } else {
                amount = BigDecimal("0.0")
                currency = Currency.getInstance(Locale.getDefault())

            }

            val element = TransactionStatusData(amount,
                currency,
                resultStatus,
                TransactionType.valueOf(it.type))
            val receipt = ReceiptData()
            val receiptList = it.receipt?.split("\n")
            if (receiptList != null) {
                receipt.receiptLines = receiptList.toTypedArray()

            }
            element.receipt = receipt
            resultList.add(element)

        }

        return resultList
    }
}