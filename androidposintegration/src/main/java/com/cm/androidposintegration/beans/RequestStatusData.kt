package com.cm.androidposintegration.beans

data class RequestStatusData (var orderReference: String? = null,
                              var page: Int = 0,
                              var size: Int = 0,
                              var sortField: String? = null,
                              var sortValue: String? = null) {

    constructor (orderReference: String) : this() {
        this.orderReference = orderReference
    }
}