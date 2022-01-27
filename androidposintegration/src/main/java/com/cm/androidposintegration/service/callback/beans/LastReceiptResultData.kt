package com.cm.androidposintegration.service.callback.beans

import android.os.Parcelable
import com.cm.androidposintegration.beans.ReceiptData
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastReceiptResultData constructor(val receiptData : ReceiptData?) : Parcelable