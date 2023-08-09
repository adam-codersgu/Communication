package com.codersguidebook.communication.ui.sms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.R

class SMSAdapter(private val activity: MainActivity) : RecyclerView.Adapter<SMSAdapter.SMSViewHolder>() {

    inner class SMSViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        internal var sender = itemView.findViewById<TextView>(R.id.sender)
        internal var body = itemView.findViewById<TextView>(R.id.body)

        init {
            itemView.isClickable = true
            itemView.setOnClickListener {
                activity.openDialog(ViewSMS(texts[adapterPosition]))
            }
            itemView.setOnLongClickListener{
                // TODO: Open the SMS options dialog here
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSAdapter.SMSViewHolder {
        return SMSViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sms_entry, parent, false))
    }
}
