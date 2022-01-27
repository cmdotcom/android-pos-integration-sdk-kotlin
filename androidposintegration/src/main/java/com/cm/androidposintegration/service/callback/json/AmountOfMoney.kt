package com.cm.androidposintegration.service.callback.json

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
class AmountOfMoney (val value: BigDecimal,
                     val currency: String) : Parcelable {

    override fun toString(): String {
        return "amount_of_money{amount=${value}, currency={${currency}"
    }
}