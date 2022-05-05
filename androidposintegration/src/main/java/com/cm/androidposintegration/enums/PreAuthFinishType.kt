package com.cm.androidposintegration.enums

import android.os.Parcelable
import com.cm.androidposintegration.intent.IntentHelper
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PreAuthFinishType(val value: String): Parcelable {
    SALE_AFTER_PRE_AUTH(IntentHelper.TYPE_SALE_PRE_AUTH),
    CANCEL_PRE_AUTH(IntentHelper.TYPE_CANCEL_PRE_AUTH);
}