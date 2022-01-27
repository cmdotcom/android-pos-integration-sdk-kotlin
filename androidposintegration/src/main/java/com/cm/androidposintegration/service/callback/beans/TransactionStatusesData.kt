package com.cm.androidposintegration.service.callback.beans

import android.os.Parcelable
import com.cm.androidposintegration.service.callback.json.TransactionStatusData
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionStatusesData constructor(val statusesInfo: List<TransactionStatusData>?,
                                               val errorMessage: String,
                                               val totalCount: Int): Parcelable