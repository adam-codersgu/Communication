package com.codersguidebook.communication.ui.callLog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.R

class CallLogAdapter(private val activity: MainActivity) : RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    inner class CallLogViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        internal var direction = itemView.findViewById<ImageView>(R.id.callDirection)
        internal var phoneNumber = itemView.findViewById<TextView>(R.id.number)
        internal var callDate = itemView.findViewById<TextView>(R.id.date)
        internal var callback = itemView.findViewById<ImageButton>(R.id.callBack)

        init {
            itemView.setOnLongClickListener{
                // TODO: Open the CallLogOptions dialog here
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        return CallLogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.call_log_entry, parent, false))
    }
}
