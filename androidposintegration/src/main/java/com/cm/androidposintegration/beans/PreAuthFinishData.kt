package com.cm.androidposintegration.beans

import com.cm.androidposintegration.enums.PreAuthFinishType
import java.math.BigDecimal
import java.util.*

class PreAuthFinishData(val type: PreAuthFinishType,
                        val originalStan: String,
                        val originalDate: Date,
                        val orderRef: String) {
    var amount: BigDecimal? = null
    var currency: Currency? = null
    var isShowReceipt: Boolean = true
}