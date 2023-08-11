package com.codersguidebook.communication.ui.sms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codersguidebook.communication.MainActivity
import com.codersguidebook.communication.R
import com.codersguidebook.communication.SMS

class SMSAdapter(private val activity: MainActivity) : RecyclerView.Adapter<SMSAdapter.SMSViewHolder>() {

    var texts = listOf<SMS>()

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
                activity.showSMSPopup(it, texts[adapterPosition])
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSAdapter.SMSViewHolder {
        return SMSViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sms_entry, parent, false))
    }

    override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
        val current = texts[position]

        holder.sender.text = current.sender
        holder.body.text = current.body
    }

    override fun getItemCount() = texts.size
}
