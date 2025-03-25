package com.cm.androidposintegration.service.callback.beans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ErrorCode(val value: Int, val desc: String) : Parcelable {
    NO_ERROR(0, "No error"),
    UNKNOWN_ERROR(-1, "Unknown error"),
    AMOUNT_INVALID(-2, "Amount used was invalid"),
    NO_INTERNET(-12, "Device is not connected to the network"),
    PRINTER_INIT_FAIL(-17, "Printer initialization failed"),
    POS_NOT_CONFIGURED(-18, "Device is not configured in gateway"),
    BAD_TIMEZONE(-24, "Timezone on the device is not correct"),
    AUTO_TIMEZONE_NOT_ENABLED(-23, "Autotimezone is not enabled on device"),
    HOST_NOT_CONNECTED(-25, "Cannot connect with the gateway"),
    MERCHANT_ORDER_REF_NOT_PRESENT(-29, "Order reference not present in request data"),
    TIMEOUT(-30, "Timeout reaching the gateway"),
    REPEATED_OPERATION(-31, "Transaction already in progress"),
    MERCHANT_ORDER_REF_TOO_LONG(-32, "Order ref exceeds allowed length"),
    AMOUNT_LIMIT_EXCEEDED(-33, "Transaction exceeds allowed limit"),
    INTERNAL_PROCESSING_ERROR(
        -34,
        "Internal processing error during transaction flow. Transaction couldn't be processed"
    ),
    INVALID_ORIGINAL_DATA(-35, "Data related with previous transaction is invalid"),
    LOW_BATTERY_LEVEL(-36, "Battery level is too low to start a transaction"),
    WRONG_EMV_AUTHORIZATION_DATE_AND_TIME(
        -37,
        "Wrong date and time in authorization request. Please reboot your device."
    ),
    RECEIPT_NOT_AVAILABLE(-38, "Receipt is not available."),

    MAX_TRANSACTIONS_COUNT_LIMIT_REACHED(-39, "Offline payments count limit is reached."),
    MAX_TOTAL_AMOUNT_LIMIT_WILL_BE_REACHED(
        -40,
        "Total offline payments sale amount limit will be reached."
    ),
    MAX_TOTAL_AMOUNT_LIMIT_REACHED(-41, "Total offline payments sale amount limit is reached."),
    MAT_INVALID_TRANSACTION_TYPE(-42, "Transaction type is not supported for offline payments."),
    MAX_TRANSACTION_AMOUNT_LIMIT_REACHED(-43, "The sale amount exceeds offline payment limit."),
    MAX_TRANSACTION_AMOUNT_LIMIT_INVALID(-46, "Offline payment limit is invalid."),
    MAX_TOTAL_AMOUNT_LIMIT_INVALID(-47, "Total offline payments sale amount limit is invalid."),
    MAX_TRANSACTIONS_COUNT_LIMIT_INVALID(-48, "Offline payments count limit is invalid."),

    TRANSACTION_STATUS_ERROR(-50, "Status information not received"),
    INFO_REQUEST_FAILED(-51, "Info Request towards the gateway has failed");

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}