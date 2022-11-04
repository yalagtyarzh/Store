package com.example.store.activities

import android.content.SharedPreferences
import android.os.Bundle
import com.example.store.R
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.example.store.adapters.MessagesAdapter
import com.example.store.commoners.AppUtils
import com.example.store.commoners.AppUtils.setDrawable
import com.example.store.commoners.BaseActivity
import com.example.store.commoners.K
import com.example.store.models.Chat
import com.example.store.models.Message
import com.example.store.utils.PreferenceHelper
import com.example.store.utils.hideView
import com.example.store.utils.showView
import com.google.firebase.database.*
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import kotlinx.android.synthetic.main.activity_chat.*
import com.example.store.utils.PreferenceHelper.get
import timber.log.Timber

class ChatActivity : BaseActivity(), View.OnClickListener {
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var chatName: String
    private lateinit var chatId: String
    private lateinit var uid1: String
    private lateinit var uid2: String
    private lateinit var chatQuery: Query
    private lateinit var prefs: SharedPreferences
    private var hasTyped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        prefs = PreferenceHelper.defaultPrefs(this)

        uid1 = intent.getStringExtra(K.MY_ID)
        uid2 = intent.getStringExtra(K.OTHER_ID)
        chatName = intent.getStringExtra(K.CHAT_NAME)
        chatId = AppUtils.chatID(uid1, uid2)

        initViews()

        chatQuery = getDatabaseReference().child(K.MESSAGES).child(chatId).orderByChild(K.TIMESTAMP)
        chatQuery.addValueEventListener(messagesValueListener)
        chatQuery.addChildEventListener(messagesChildListener)
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = chatName
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextListener()
        send.setImageDrawable(setDrawable(this, FontAwesome.Icon.faw_paper_plane, R.color.colorPrimaryLight, 22))

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()

        messagesAdapter = MessagesAdapter()
        rv.adapter = messagesAdapter
    }

    // listens for changes on message EditText
    private fun editTextListener() {
        message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty() || s.isBlank()) {
                    hasTyped = false
                    send.setImageDrawable(setDrawable(this@ChatActivity, FontAwesome.Icon.faw_paper_plane, R.color.colorPrimaryLight, 22))

                } else {
                    hasTyped = true
                    send.setImageDrawable(setDrawable(this@ChatActivity, FontAwesome.Icon.faw_paper_plane, R.color.colorPrimary, 22))

                }
            }
        })
    }

    private val messagesValueListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Timber.e("Error fetching messages: $p0")
            noMessages()
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                hasMessages()
            } else {
                noMessages()
            }
        }
    }

    private val messagesChildListener = object : ChildEventListener {

        override fun onCancelled(p0: DatabaseError) {
            Timber.e("Child listener cancelled: $p0")
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            Timber.e("Message moved: ${p0.key}")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            Timber.e("Message changed: ${p0.key}")
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val message = p0.getValue(Message::class.java)
            messagesAdapter.addMessage(message!!)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            Timber.e("Message removed: ${p0.key}")
        }
    }

    // Upload message
    private fun sendMessage() {
        val ref = getDatabaseReference().child(K.MESSAGES).child(chatId)
        val key = ref.push().key

        val msg = Message()
        msg.id = key
        msg.senderId = getUid()
        msg.chatId = chatId
        msg.time = System.currentTimeMillis()
        msg.message = message.text.toString().trim()

        ref.child(key!!).setValue(msg).addOnSuccessListener {
            message.setText("")
            updateChats()
        }
    }

    // Updates Chat object in chats
    private fun updateChats() {
        val chat = Chat()
        chat.id = chatId
        chat.username = chatName
        chat.time = System.currentTimeMillis()
        chat.message = message.text.toString().trim()
        chat.senderId = getUid()

        getDatabaseReference().child(K.CHATS).child(getUid()).child(chatId).setValue(chat)

        chat.username = prefs[K.NAME]
        getDatabaseReference().child(K.CHATS).child(uid2).child(chatId).setValue(chat)
    }

    private fun hasMessages() {
        empty?.hideView()
        rv?.showView()
    }

    private fun noMessages() {
        rv?.hideView()
        empty?.showView()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            send.id -> if (hasTyped) {
                sendMessage()
                updateChats()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        chatQuery.removeEventListener(messagesChildListener)
        chatQuery.removeEventListener(messagesValueListener)
    }
}
