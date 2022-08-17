package com.cm.androidposintegration.activity

import androidx.lifecycle.ViewModel

class IntegrationViewModel: ViewModel() {

    private var operationType = -1

    fun setOperationType(opType: Int) {
        operationType = opType

    }

    fun getOperationType(): Int {
        return operationType
    }
}