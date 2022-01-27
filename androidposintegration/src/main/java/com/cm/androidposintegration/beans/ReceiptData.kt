package com.cm.androidposintegration.beans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptData constructor(var receiptLines: Array<String>? = null,
                                   var signature: ByteArray? = null) : Parcelable