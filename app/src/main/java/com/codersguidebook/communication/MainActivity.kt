package com.codersguidebook.communication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.codersguidebook.communication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val READ_STORAGE_REQUEST_CODE = 1
    }

    private val communicationViewModel: CommunicationViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_STORAGE_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCallLogs()
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
        val readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val readCallLogPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)

        if (readStoragePermission != PackageManager.PERMISSION_GRANTED ||
            readCallLogPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG), READ_STORAGE_REQUEST_CODE)
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
}