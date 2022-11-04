package com.example.store.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.store.R
import com.example.store.commoners.K
import com.example.store.databinding.ItemMessageBinding
import com.example.store.databinding.ItemMessageMeBinding
import com.example.store.models.Message
import com.example.store.utils.inflate
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages = mutableListOf<Message>()

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun lastPosition(): Int = messages.size - 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == K.ME) {
            ChatMeHolder(parent.inflate(R.layout.item_message_me))
        } else {
            ChatHolder(parent.inflate(R.layout.item_message))
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == FirebaseAuth.getInstance().currentUser!!.uid) {
            K.ME
        } else {
            K.OTHER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatHolder) {
            holder.bind(messages[position])
        } else if (holder is ChatMeHolder) {
            holder.bind(messages[position])
        }

    }

    class ChatMeHolder(private val binding: ItemMessageMeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.message = message
        }

    }

    class ChatHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.message = message
        }

    }
}