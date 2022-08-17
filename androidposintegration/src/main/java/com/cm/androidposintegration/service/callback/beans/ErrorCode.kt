package com.cm.androidposintegration.service.callback.beans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ErrorCode(val value: Int, val desc: String): Parcelable {

    NO_ERROR (0, "No error"),
    UNKNOWN_ERROR (-1, "Unknown error"),
    AMOUNT_INVALID (-2, "Amount used was invalid"),
    NO_INTERNET (-12, "Device is not connected to the network"),
    PRINTER_INIT_FAIL (-17, "Printer initialization failed"),
    POS_NOT_CONFIGURED (-18, "Device is not configured in gateway"),
    BAD_TIMEZONE (-24, "Timezone on the device is not correct"),
    AUTO_TIMEZONE_NOT_ENABLED(-23, "Autotimezone is not enabled on device"),
    HOST_NOT_CONNECTED (-25, "Cannot connect with the gateway"),
    MERCHANT_ORDER_REF_NOT_PRESENT (-29, "Order reference not present in request data"),
    TIMEOUT (-30, "Timeout reaching the gateway"),
    REPEATED_OPERATION (-31, "Transaction already in progress"),
    MERCHANT_ORDER_REF_TOO_LONG (-32, "Order ref exceeds allowed length"),
    AMOUNT_LIMIT_EXCEEDED (-33, "Transaction exceeds allowed limit"),
    EXCEPTION_THROWN (-34, "Exception thrown during transaction flow. Transaction couldn't be processed"),
    INVALID_ORIGINAL_DATA(-35, "Data related with previous transaction is invalid"),
    LOW_BATTERY_LEVEL(-36, "Battery level is too low to start a transaction"),
    WRONG_EMV_AUTHORIZATION_DATE_AND_TIME(-37, "Wrong date and time in authorization request. Please reboot your device."),
    TRANSACTION_STATUS_ERROR (-50, "Status information not received"),
    INFO_REQUEST_FAILED (-51, "Info Request towards the gateway has failed");

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}