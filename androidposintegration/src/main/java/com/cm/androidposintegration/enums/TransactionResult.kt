package com.cm.androidposintegration.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TransactionResult (val description: String): Parcelable {

    SUCCESS ("Operation was successful"),
    CANCELED ("Operation was canceled"),
    AUTHORIZATION_FAILURE ("Operation did not have authorization"),
    AUTHORIZATION_TIMEOUT ("Timeout in last operation authorization"),
    CARD_BLOCKED ("Card used is blocked"),
    CARD_INVALID ("Card used is invalid"),
    DECLINED_BY_CARD ("Operation was declined by card"),
    INSUFICIENT_FUNDS ("Not sufficient funds to perform last authorization"),
    FAILED ("Operation failed"),
    AMOUNT_EXCEEDED ("Amount of las operation exceeded the limit"),
    HOST_BLOCKED_PRINT_RECEIPT ("Operation couldn’t be performed. Receipt of the last transaction pending"),
    REQUEST_RECEIPT("Request Receipt of current Transaction");

    override fun toString(): String {
        return "${this.name}(${this.description})"
    }
}