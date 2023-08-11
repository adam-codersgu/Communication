package com.codersguidebook.communication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class SMSBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val messages = android.provider.Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNotEmpty()) Toast.makeText(context,
                context.getString(R.string.new_sms_received, messages[0]?.messageBody), Toast.LENGTH_SHORT).show()
            context.sendBroadcast(Intent("SMS_RECEIVED"))
        }
    }
}
