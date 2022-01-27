package com.cm.androidposintegration.service.callback.json

import android.os.Parcelable
import com.cm.androidposintegration.beans.ReceiptData
import com.cm.androidposintegration.enums.TransactionResult
import com.cm.androidposintegration.enums.TransactionType
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
class TransactionStatusData (val amount: BigDecimal,
                             val currency: Currency,
                             val result: TransactionResult,
                             val type: TransactionType,
                             var receipt: ReceiptData? = null) : Parcelable {

    override fun toString(): String {
        return "Transaction Status: {amount=${amount}, " +
                "currency=${currency.currencyCode}, " +
                "result=${result}, " +
                "type=${type}, " +
                "receipt=${receipt}}"
    }
}