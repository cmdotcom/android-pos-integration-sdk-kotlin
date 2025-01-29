package com.payplaza.androidposintegration.service

import android.app.Activity
import android.app.Instrumentation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.cm.androidposintegration.BuildConfig
import com.cm.androidposintegration.activity.IntegrationActivity
import com.cm.androidposintegration.beans.TransactionData
import com.cm.androidposintegration.enums.TransactionType
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_ERROR_CODE
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_INFORMATION_VALUE_TRANSACTION
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_INTERNAL_INTENT_TYPE
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_MAX_OFFLINE_SALE_AMOUNT
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_MAX_OFFLINE_SALE_AMOUNT_PER_TRANSACTION
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_MAX_OFFLINE_TRANSACTIONS_COUNT
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_MERCHANT_RECEIPT
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_ORD_REF
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_SDK_VERSION
import com.cm.androidposintegration.intent.IntentHelper.EXTRA_TRANSACTION_RESULT
import com.cm.androidposintegration.service.PosIntegrationServiceImpl
import com.cm.androidposintegration.service.callback.TransactionCallback
import com.cm.androidposintegration.service.callback.beans.ErrorCode
import com.cm.androidposintegration.service.callback.beans.TransactionResultData
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.*

@RunWith(AndroidJUnit4::class)
class PosIntegrationServiceImplTests {

    @get:Rule
    var activityRule: ActivityScenarioRule<IntegrationActivity> =
        ActivityScenarioRule(IntegrationActivity::class.java)

    private lateinit var targetContext: Context
    private lateinit var receipt: Array<String>
    private val maxStoredTransactionsCount = 50
    private val maxStoredTransactionSaleAmount = BigDecimal(25000.0)
    private val maxStoredTransactionSaleAmountPerTransaction = BigDecimal(250.0)

    inner class TestBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            assert(p1!!.hasExtra(EXTRA_TRANSACTION_RESULT))
            assert(p1.hasExtra(EXTRA_ERROR_CODE))
        }

    }

    @Before
    fun setUp() {
        Intents.init()
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        receipt = arrayOf(" ", "Development 3.0", "Payment ok", "STAN 897654")
    }

    @After
    fun finish() {
        Intents.release()
    }

    @Test
    fun doTransactionTest() {
        val service = PosIntegrationServiceImpl(targetContext)
        val callback = object : TransactionCallback {

            override fun onResult(data: TransactionResultData) {
                // Default implementation
            }

            override fun onError(error: ErrorCode) {
                // Default implementation
            }

            override fun onCrash() {
                // Default implementation
            }
        }

        val data = TransactionData(
            TransactionType.PURCHASE,
            BigDecimal(0.50),
            Currency.getInstance("EUR"),
            "0123-12345"
        ).apply {
            maxOfflineTransactionsCount = maxStoredTransactionsCount
            maxOfflineSaleAmount = maxStoredTransactionSaleAmount
            maxOfflineSaleAmountPerTransaction = maxStoredTransactionSaleAmountPerTransaction
        }

        service.doTransaction(data, callback)


        // Check that doTransaction calls the ecr app
        intended(
            allOf(
                hasComponent(IntegrationActivity::class.java.name),
                hasExtra(EXTRA_INTERNAL_INTENT_TYPE, EXTRA_INFORMATION_VALUE_TRANSACTION),
                hasExtra(EXTRA_SDK_VERSION, BuildConfig.VERSION_NAME),
                hasExtra(EXTRA_MAX_OFFLINE_TRANSACTIONS_COUNT, maxStoredTransactionsCount),
                hasExtra(EXTRA_MAX_OFFLINE_SALE_AMOUNT, maxStoredTransactionSaleAmount),
                hasExtra(
                    EXTRA_MAX_OFFLINE_SALE_AMOUNT_PER_TRANSACTION,
                    maxStoredTransactionSaleAmountPerTransaction
                )
            )
        )

        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_ORD_REF, "0123-12345")
        resultIntent.putExtra(EXTRA_TRANSACTION_RESULT, "success")
        resultIntent.putExtra(EXTRA_ERROR_CODE, 0)
        resultIntent.putExtra(EXTRA_MERCHANT_RECEIPT, receipt)
        resultIntent.putExtra(EXTRA_MAX_OFFLINE_TRANSACTIONS_COUNT, maxStoredTransactionsCount)
        resultIntent.putExtra(EXTRA_MAX_OFFLINE_SALE_AMOUNT, maxStoredTransactionSaleAmount)
        resultIntent.putExtra(
            EXTRA_MAX_OFFLINE_SALE_AMOUNT_PER_TRANSACTION,
            maxStoredTransactionSaleAmountPerTransaction
        )
        val activityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)

        intending(hasAction(BuildConfig.ACTION_TRANSACTION)).respondWith(activityResult)

    }
}