package com.cm.androidposintegration.service.callback.json

class TransactionStatusSimpleData (val amount_of_money: AmountOfMoney?,
                                   val result: String,
                                   var receipt: String?,
                                   val type : String) {


    override fun toString(): String {
        return "Transaction Status: {amountOfMoney=${amount_of_money}, " +
                "result=${result}, " +
                "receipt=${receipt}, " +
                "type=${type}}"
    }
}