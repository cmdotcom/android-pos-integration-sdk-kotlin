package com.payplaza.androidposintegration.service

import com.cm.androidposintegration.enums.TransactionResult
import com.cm.androidposintegration.service.callback.json.StatusResponseParser
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.util.*


class JsonTests {

    @Test
    fun testJsonParser() {
        val list = StatusResponseParser.getTxStatuses("[{\"amount_of_money\":{\"value\":10.0,\"currency\":\"EUR\"},\"result\":\"APPROVED\",\"receipt\":\"   Payplaza Dev Shop    \\n    Programming fun     \\n    ---------------     \\n Keep up the good work  \\n\\nTerminal:       00000001\\nMerchant:     1234567890\\nECR:      E_SUNMI_P2lite\\n          _PL02196J00565\\nSTAN:             114262\\n\\n             CONTACTLESS\\nAID:      A0000000031010\\n            VISA DESPUES\\nKaart:      ********3509\\nKaartnr:               1\\n\\nDatum:23-09-21  15:15:05\\nAC:     BDD9FB400308B006\\nProcessor:       OmniPay\\nAuth. code:       837F37\\nAuth. resp. code:     00\\n\\nBEDRAG:        EUR 10,00\\nREF:               00-01\\n\\n    Betaling akkoord    \\n\\nKAARTHOUDER BON         \\n\\nwww.payplaza.com\\r\\nline2\\r\",\"type\":\"PURCHASE\"}]")
        val list2 = StatusResponseParser.getTxStatuses("[{\"amount_of_money\":{\"value\":10.0,\"currency\":\"EUR\"},\"result\":\"APPROVED\",\"receipt\":\"   Payplaza Dev Shop    \\n    Programming fun     \\n    ---------------     \\n Keep up the good work  \\n\\nTerminal:       00000001\\nMerchant:     1234567890\\nECR:      E_SUNMI_P2lite\\n          _PL02196J00565\\nSTAN:             114262\\n\\n             CONTACTLESS\\nAID:      A0000000031010\\n            VISA DESPUES\\nKaart:      ********3509\\nKaartnr:               1\\n\\nDatum:23-09-21  15:15:05\\nAC:     BDD9FB400308B006\\nProcessor:       OmniPay\\nAuth. code:       837F37\\nAuth. resp. code:     00\\n\\nBEDRAG:        EUR 10,00\\nREF:               00-01\\n\\n    Betaling akkoord    \\n\\nKAARTHOUDER BON         \\n\\nwww.payplaza.com\\r\\nline2\\r\",\"type\":\"PURCHASE\"}]")

        assertEquals(list[0].currency.getSymbol(), Currency.getInstance("EUR").getSymbol())
        assertEquals(list[0].amount, BigDecimal("10.0"))
        assertEquals(list[0].result, TransactionResult.SUCCESS)
        assertEquals(list[0].receipt?.receiptLines?.get(0), "   Payplaza Dev Shop    ")

        assertEquals(list[0].currency.getSymbol(), Currency.getInstance("EUR").getSymbol())
        assertEquals(list[0].amount, BigDecimal("10.0"))
        assertEquals(list[0].result, TransactionResult.SUCCESS)
        assertEquals(list[0].receipt?.receiptLines?.get(0), "   Payplaza Dev Shop    ")


    }
}