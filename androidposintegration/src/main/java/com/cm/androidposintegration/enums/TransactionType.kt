package com.cm.androidposintegration.enums

import android.os.Parcelable
import com.cm.androidposintegration.intent.IntentHelper
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TransactionType(val value: String) : Parcelable {
    PURCHASE(IntentHelper.TYPE_PURCHASE), REFUND(IntentHelper.TYPE_REFUND);
}