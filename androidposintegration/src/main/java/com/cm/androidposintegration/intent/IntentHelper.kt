package com.cm.androidposintegration.intent

object IntentHelper {

    const val INTEGRATION_BROADCAST_INTENT = "com.payplaza.integration.INTEGRATION_BROADCAST"

    // Transaction flow extras
    const val FLOW_PAYMENT = "payment"
    const val FLOW_RECEIPT = "receipt"
    const val FLOW_TOTALS = "totals"

    // Transaction type extras
    const val TYPE_PURCHASE = "purchase"
    const val TYPE_REFUND = "refund"
    const val TYPE_PRE_AUTH = "pre_auth"
    const val TYPE_SALE_PRE_AUTH = "sale_pre_auth"
    const val TYPE_CANCEL_PRE_AUTH = "cancel_pre_auth"

    // Payment Request extras.
    const val EXTRA_TRANSACTION_FLOW = "com.payplaza.extra.TRANSACTION_FLOW"
    const val EXTRA_TRANSACTION_TYPE = "com.payplaza.extra.TRANSACTION_TYPE"
    const val EXTRA_CURRENCY_CODE = "com.payplaza.extra.CURRENCY_CODE"
    const val EXTRA_AMOUNT = "com.payplaza.extra.AMOUNT"
    const val EXTRA_TIPPING = "com.payplaza.extra.TIPPING"
    const val EXTRA_CAPTURE_SIGNATURE = "com.payplaza.extra.CAPTURE_SIGNATURE"
    const val EXTRA_LANGUAGE = "com.payplaza.extra.LANGUAGE"
    const val EXTRA_ORD_REF = "com.payplaza.extra.ORDERREF"
    const val EXTRA_STAN = "com.payplaza.extra.STAN"
    const val EXTRA_TRANSACTION_DATE = "com.payplaza.extra.TRANSACTION_DATE"
    const val EXTRA_PAGE = "com.payplaza.extra.PAGE"
    const val EXTRA_SIZE = "com.payplaza.extra.SIZE"
    const val EXTRA_SORT_FIELD = "com.payplaza.extra.SORT_FIELD"
    const val EXTRA_SORT_VALUE = "com.payplaza.extra.SORT_VALUE"
    const val EXTRA_SHOW_RECEIPT = "com.payplaza.extra.SHOW_RECEIPT"
    const val EXTRA_USE_PROC_STYLE_PROTOCOL = "com.payplaza.extra.USE_PROC_STYLE_PROTOCOL"
    const val EXTRA_DAY_TOTALS_FROM = "com.payplaza.extra.DAY_TOTALS_FROM"

    // Payment Response extras.
    const val EXTRA_TRANSACTION_RESULT = "com.payplaza.extra.TRANSACTION_RESULT"
    const val EXTRA_ERROR_CODE = "com.payplaza.extra.ERROR_CODE"
    const val EXTRA_CUSTOMER_RECEIPT = "com.payplaza.extra.CUSTOMER_RECEIPT"
    const val EXTRA_CUSTOMER_SIGNATURE = "com.payplaza.extra.CUSTOMER_SIGNATURE"
    const val EXTRA_MERCHANT_RECEIPT = "com.payplaza.extra.MERCHANT_RECEIPT"
    const val EXTRA_MERCHANT_SIGNATURE = "com.payplaza.extra.MERCHANT_SIGNATURE"

    // Statuses Response extras.
    const val EXTRA_TRANSACTION_STATUS_DATA = "com.payplaza.extra.TRANSACTION_STATUS_DATA"
    const val EXTRA_TRANSACTION_STATUS_ERROR = "com.payplaza.extra.ERROR_MESSAGE"
    const val EXTRA_TRANSACTION_STATUS_TOTAL_COUNT = "com.payplaza.extra.TRANSACTION_STATUS_TOTAL_COUNT"

    const val EXTRA_INTERNAL_OPERATION_RESULT = "com.payplaza.extra.OPERATION_RESULT"
    const val EXTRA_INTERNAL_INTENT_TYPE = "com.payplaza.extra.INTEGRATION_OPERATION_TYPE"
    const val EXTRA_INFORMATION_RECEIVED_TYPE = "com.payplaza.extra.integration.information.received.type"
    const val EXTRA_INFORMATION_VALUE_TRANSACTION = "transaction"
    const val EXTRA_INFORMATION_VALUE_STATUSES = "statuses"
    const val EXTRA_INFORMATION_VALUE_RECEIPT = "receipt"
    const val EXTRA_INFORMATION_VALUE_TOTALS = "totals"
    const val EXTRA_INFORMATION_VALUE_INFO = "info"
    const val EXTRA_AUTH_RESPONSE_CODE = "com.payplaza.extra.AUTH_RESPONSE_CODE"
    const val EXTRA_CARD_ENTRY_MODE = "com.payplaza.extra.CARD_ENTRY_MODE"
    const val EXTRA_ECR_ID = "com.payplaza.extra.ECR_ID"
    const val EXTRA_PROCESSOR_NAME = "com.payplaza.extra.PROCESSOR_NAME"
    const val EXTRA_TRANSACTION_DATE_TIME = "com.payplaza.extra.TRANSACTION_DATE_TIME"
    const val EXTRA_TRANSACTION_ID = "com.payplaza.extra.TRANSACTION_ID"
    const val EXTRA_CARD_SCHEME = "com.payplaza.extra.CARD_SCHEME"
    const val EXTRA_AID = "com.payplaza.extra.AID"
    const val EXTRA_CARD_NUMBER = "com.payplaza.extra.CARD_NUMBER"
    const val EXTRA_CARD_TYPE = "com.payplaza.extra.CARD_TYPE"
    const val EXTRA_TIP_AMOUNT = "com.payplaza.extra.TIP_AMOUNT"

    const val EXTRA_INFO_RESULT = "com.payplaza.extra.INFOR_RESULT"
    const val EXTRA_STORE_NAME = "com.payplaza.extra.STORE_NAME"
    const val EXTRA_STORE_ADDRESS = "com.payplaza.extra.STORE_ADDRESS"
    const val EXTRA_STORE_CITY = "com.payplaza.extra.STORE_CITY"
    const val EXTRA_STORE_ZIP_CODE = "com.payplaza.extra.STORE_ZIP_CODE"
    const val EXTRA_STORE_LANGUAGE = "com.payplaza.extra.STORE_LANGUAGE"
    const val EXTRA_STORE_COUNTRY = "com.payplaza.extra.STORE_COUNTRY"
    const val EXTRA_STORE_CURRENCY = "com.payplaza.extra.STORE_CURRENCY"
    const val EXTRA_DEVICE_SERIAL = "com.payplaza.extra.DEVICE_SERIAL"
    const val EXTRA_TERMINAL_VERSION_NUMBER = "com.payplaza.extra.TERMINAL_VERSION_NUMBER"

    const val MAX_ORDER_REF_LENGTH = 14
}
