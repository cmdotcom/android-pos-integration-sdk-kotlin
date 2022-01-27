package com.cm.androidposintegration.intent

import android.content.Intent
import java.math.BigDecimal
import java.util.*

object IntentUtil {

    fun getBigDecimal(intent: Intent, key: String?): Optional<BigDecimal> {
        val serializableExtra = intent.getSerializableExtra(key)
        return if (serializableExtra != null && serializableExtra is BigDecimal) {
            Optional.of(serializableExtra)
        } else Optional.empty()
    }

    fun getDate(intent: Intent, key: String?): Optional<Date> {
        val serializableExtra = intent.getSerializableExtra(key)
        return if (serializableExtra != null && serializableExtra is Date) {
            Optional.of(serializableExtra)
        } else Optional.empty()
    }
}