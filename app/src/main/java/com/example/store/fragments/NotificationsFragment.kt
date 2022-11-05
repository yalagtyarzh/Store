package com.example.store.fragments


import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.store.R
import com.example.store.adapters.NotificationsAdapter
import com.example.store.commoners.BaseFragment
import com.example.store.models.Notification
import com.example.store.utils.RecyclerFormatter
import kotlinx.android.synthetic.main.fragment_notifications.view.*


class NotificationsFragment : BaseFragment() {
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(v: View) {
        v.rv.setHasFixedSize(true)
        v.rv.layoutManager = LinearLayoutManager(activity!!)
        v.rv.itemAnimator = DefaultItemAnimator()
        v.rv.addItemDecoration(RecyclerFormatter.SimpleDividerItemDecoration(activity!!))

        notificationsAdapter = NotificationsAdapter()
        v.rv.adapter = notificationsAdapter
        v.rv.showShimmerAdapter()

        Handler().postDelayed({
            v.rv.hideShimmerAdapter()
            loadSample()
        }, 2500)
    }

    private fun loadSample() {
        val notif1 = Notification()
        notif1.actionType = "Welcome"
        notif1.summary = "Welcome"
        notif1.time = System.currentTimeMillis()
        notif1.avatar = R.drawable.person
        notificationsAdapter.addNotif(notif1)
    }

}
