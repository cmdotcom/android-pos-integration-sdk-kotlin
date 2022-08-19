package com.cm.androidposintegration.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cm.androidposintegration.BuildConfig
import com.cm.androidposintegration.intent.IntentHelper
import com.cm.androidposintegration.service.callback.RequestId

open class IntegrationActivity : AppCompatActivity() {

    private var TAG = IntegrationActivity::class.java.simpleName
    private var intentPayment : Intent? = null

    private lateinit var viewModel : IntegrationViewModel

    private val getTerminalResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d(TAG, "Received response from Payplaza apps ${viewModel.getOperationType()}, ${result.resultCode}, ${result.data}")
            val internalBroadcast = Intent(IntentHelper.INTEGRATION_BROADCAST_INTENT)
            internalBroadcast.putExtra(IntentHelper.EXTRA_INTERNAL_OPERATION_RESULT, result.resultCode)

            // Process the information received in the intent
            if (result.data != null) {
                internalBroadcast.putExtras(result.data!!)
            }

            sendInternalBroadcast(viewModel.getOperationType(), internalBroadcast)
            sendBroadcast(internalBroadcast)
            finish()
        }


    /**
     * Creates the intent to use for payplaza apps and the requestId to use
     * so at response time, androidposIntegration knows what has been requested
     * to payplaza apps (Transaction, receipt, totals, statuses)
     * @param operationType is the operation type received on the intent that created this activity
     */
    private fun createIntentAndOperationType(operationType: String) {
        var requestId = -1
        when (operationType) {
            IntentHelper.EXTRA_INFORMATION_VALUE_TRANSACTION -> {
                intentPayment = Intent(BuildConfig.ACTION_TRANSACTION)
                requestId = RequestId.TRANSACTION_REQUEST_ID

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_STATUSES -> {
                Log.d(TAG, "Sending statuses intent")
                intentPayment = Intent(BuildConfig.ACTION_STATUSES)
                requestId = RequestId.TRANSACTION_STATUS_REQUEST_ID

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_RECEIPT -> {
                intentPayment = Intent(BuildConfig.ACTION_TRANSACTION)
                requestId = RequestId.LAST_RECEIPT_REQUEST_ID

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_TOTALS -> {
                intentPayment = Intent(BuildConfig.ACTION_TRANSACTION)
                requestId = RequestId.DAY_TOTALS

            }

            IntentHelper.EXTRA_INFORMATION_VALUE_INFO -> {
                intentPayment = Intent(BuildConfig.ACTION_INFO)
                requestId = RequestId.INFO_REQUEST

            }
        }

        viewModel.setOperationType(requestId)
    }

    private fun createBroadcastForError(): Intent {
        Log.w(TAG, "No payplaza payment apps in the device, intent for kicking them wrongly created or Terminal crashed")
        Log.w(TAG, "Starting onCrash callback mechanism.  ${viewModel.getOperationType()}")
        val internalBroadcast = Intent(IntentHelper.INTEGRATION_BROADCAST_INTENT)
        sendInternalBroadcast(viewModel.getOperationType(), internalBroadcast)
        return internalBroadcast
    }

    private fun checkAndSendIntent() {
        if (intentPayment != null) {
            intentPayment!!.putExtras(intent)
            Log.d(TAG, "Sending intent $intentPayment")
            if(intentPayment!!.resolveActivity(packageManager) != null) {
                getTerminalResult.launch(intentPayment) //startActivityForResult(intentPayment, requestId)

            } else {
                sendBroadcast(createBroadcastForError())
                finish()

            }
        } else {
            sendBroadcast(createBroadcastForError())
            finish()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            Log.d(TAG, "Saved Instance already")
        }
        viewModel =  ViewModelProvider(this).get(IntegrationViewModel::class.java)
        val operationType = intent.getStringExtra(IntentHelper.EXTRA_INTERNAL_INTENT_TYPE)
        if (savedInstanceState == null && operationType != null) {
            Log.d(TAG,"\"Created\" activity for kick payplaza Terminal")
            createIntentAndOperationType(operationType)
            checkAndSendIntent()

        } else if (savedInstanceState == null && operationType == null) {
            Log.d(TAG,"No OperationType received. Ending the request")
            sendBroadcast(createBroadcastForError())
            finish()

        } else {
            Log.d(TAG, "Activity already created. Not kicking payplaza Terminal")

        }

    }

    /**
     * Sends an internal broadcast with the result of the operation that has been requested
     * It uses the requestCode to know which operation has been requested
     * @param requestCode request Id that was sent to payplaza apps
     * @param internalBroadcast is the current broadcast intent that is going to be sent internally
     */
    private fun sendInternalBroadcast(requestCode: Int, internalBroadcast: Intent) {
        when (requestCode) {
            RequestId.TRANSACTION_REQUEST_ID -> {
                internalBroadcast.putExtra(IntentHelper.EXTRA_INFORMATION_RECEIVED_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_TRANSACTION)

            }

            RequestId.TRANSACTION_STATUS_REQUEST_ID -> {
                internalBroadcast.putExtra(IntentHelper.EXTRA_INFORMATION_RECEIVED_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_STATUSES)

            }

            RequestId.LAST_RECEIPT_REQUEST_ID -> {
                internalBroadcast.putExtra(IntentHelper.EXTRA_INFORMATION_RECEIVED_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_RECEIPT)

            }

            RequestId.DAY_TOTALS -> {
                internalBroadcast.putExtra(IntentHelper.EXTRA_INFORMATION_RECEIVED_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_TOTALS)

            }

            RequestId.INFO_REQUEST -> {
                internalBroadcast.putExtra(IntentHelper.EXTRA_INFORMATION_RECEIVED_TYPE, IntentHelper.EXTRA_INFORMATION_VALUE_INFO)

            }

            else -> Log.w(TAG,"Request Id Code in the response not recognized")

        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "Received response from Payplaza apps $requestCode, $resultCode, $data")
        val internalBroadcast = Intent(IntentHelper.INTEGRATION_BROADCAST_INTENT)
        internalBroadcast.putExtra(IntentHelper.EXTRA_INTERNAL_OPERATION_RESULT, resultCode)

        // Process the information received in the intent
        if (data != null) {
            internalBroadcast.putExtras(data)
        }

        sendInternalBroadcast(requestCode, internalBroadcast)
        sendBroadcast(internalBroadcast)
        finish()
    }*/


}