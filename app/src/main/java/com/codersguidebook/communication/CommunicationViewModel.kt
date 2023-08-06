package com.codersguidebook.communication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommunicationViewModel : ViewModel() {
    var callLog = MutableLiveData<List<CallLogEvent>>()
}
