package com.codersguidebook.communication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.Telephony
import android.telephony.SmsManager
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.codersguidebook.communication.databinding.ActivityMainBinding
import com.codersguidebook.communication.ui.sms.SendSMS
import com.codersguidebook.communication.ui.sms.ViewSMS
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val READ_CALL_LOG_REQUEST_CODE = 1
        const val READ_SMS_REQUEST_CODE = 2
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getTexts()
        }
    }

    private val communicationViewModel: CommunicationViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_phone, R.id.navigation_call_log, R.id.navigation_sms))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 0)
        }

        registerReceiver(broadcastReceiver, IntentFilter("SMS_RECEIVED"), RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_CALL_LOG_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCallLogs()
            READ_SMS_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getTexts()
        }
    }

    fun callNumber(number: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } else ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CALL_PHONE), 0)
    }

    fun getCallLogs() {
        val readCallLogPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)

        if (readCallLogPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), READ_CALL_LOG_REQUEST_CODE)
            return
        }

        val cursor = application.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC")

        val callLog = mutableListOf<CallLogEvent>()

        cursor?.use {
            val number = it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val type = it.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val date = it.getColumnIndexOrThrow(CallLog.Calls.DATE)
            while (it.moveToNext()) {
                val phoneNumber = cursor.getString(number)
                val callType = cursor.getString(type)
                val callDate = cursor.getLong(date)
                val callDateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(callDate))
                val direction = when (callType.toInt()) {
                    CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> "MISSED"
                    else -> null
                }
                val entry = CallLogEvent(direction, phoneNumber, callDateString)
                callLog.add(entry)
            }
        }

        cursor?.close()

        communicationViewModel.callLog.value = callLog
    }

    fun showCallLogPopup(view: View, phoneNumber: String) {
        PopupMenu(this, view).apply {
            inflate(R.menu.call_log)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.make_call -> {
                        callNumber(phoneNumber)
                        true
                    }
                    R.id.send_sms -> {
                        openDialog(SendSMS(phoneNumber))
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
            show()
        }
    }

    fun openDialog(dialog: DialogFragment) = dialog.show(supportFragmentManager, null)

    fun getTexts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), READ_SMS_REQUEST_CODE)
            return
        }

        val texts = mutableListOf<SMS>()

        val cursor = application.contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, "date DESC")
        cursor?.use {
            val senderColumn = it.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS)
            val bodyColumn = it.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY)

            while (it.moveToNext()) {
                val sender = it.getString(senderColumn) ?: getString(R.string.error_sender)
                val body = it.getString(bodyColumn) ?: getString(R.string.error_body)
                val sms = SMS(sender, body)
                texts.add(sms)
            }
        }

        communicationViewModel.texts.value = texts.take(20)
    }

    fun showSMSPopup(view: View, text: SMS) {
        PopupMenu(this, view).apply {
            inflate(R.menu.sms)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.view_message -> {
                        openDialog(ViewSMS(text))
                        true
                    }
                    R.id.reply -> {
                        openDialog(SendSMS(text.sender))
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
            show()
        }
    }

    fun sendSMS(number: String, message: String): Boolean {
        if (number.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_sending_sms), Toast.LENGTH_LONG).show()
            return false
        }
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED) {
            val smsManager = getSystemService(SmsManager::class.java)
            // 160 characters is typically the maximum size per message
            if (message.length > 160) {
                val messages: ArrayList<String> = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(number, null, messages, null, null)
            } else smsManager.sendTextMessage(number, null, message, null, null)
            true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 0)
            false
        }
    }
}