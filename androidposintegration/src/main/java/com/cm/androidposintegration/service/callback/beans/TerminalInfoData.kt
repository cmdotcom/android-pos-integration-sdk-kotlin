package com.cm.androidposintegration.service.callback.beans

import android.os.Parcelable
import com.cm.androidposintegration.enums.TransactionResult
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class TerminalInfoData (val transactionResult: TransactionResult,
                             var storeName: String? = null,
                             var storeAddress: String? = null,
                             var storeCity: String? = null,
                             var storeZipCode: String? = null,
                             var storeLanguage: String? = null,
                             var storeCountry: String? = null,
                             var storeCurrency: Currency? = null,
                             var deviceSerialNumber: String? = null,
                             var versionNumber: String? = null ) : Parcelable {

    override fun toString() : String {
        return "TerminalInfoData { 'TerminalInfoData' : $transactionResult, " +
                "'storeName' : $storeName,  " +
                "'storeAddress' : $storeAddress, " +
                "'storeCity' : $storeCity, " +
                "'storeZipCode' : ${storeZipCode}, " +
                "'storeLanguage' : $storeLanguage, " +
                "'storeCountry' : ${storeCountry}, " +
                "'storeCurrency' : " +
                if (storeCurrency != null) {  "${storeCurrency!!.getDisplayName(Locale.getDefault())} }" }
                else {"null" }  +
                "'deviceSerialNumber' : ${deviceSerialNumber}, " +
                "'versionNumber' : ${versionNumber} }"
    }
}