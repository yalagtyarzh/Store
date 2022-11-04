package com.example.store.fragments


import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.example.store.R
import android.view.ViewGroup

import com.example.store.adapters.BookingsAdapter
import com.example.store.commoners.BaseFragment
import com.example.store.commoners.K
import com.example.store.models.Booking
import com.example.store.utils.RecyclerFormatter
import com.example.store.utils.hideView
import com.example.store.utils.showView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_bookings.*
import timber.log.Timber

class OrdersCarsFragment : BaseFragment() {
    private lateinit var bookingsAdapter: BookingsAdapter
    private lateinit var ordersQuery: Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        ordersQuery = getDatabaseReference().child(K.BOOKINGS).child(getUid())
        return inflater.inflate(R.layout.fragment_bookings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        ordersQuery.addValueEventListener(carsValueListener)
        ordersQuery.addChildEventListener(carsChildListener)
    }

    private fun initViews(v: View) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.itemAnimator = DefaultItemAnimator()
        rv.addItemDecoration(RecyclerFormatter.SimpleDividerItemDecoration(activity!!))

        bookingsAdapter = BookingsAdapter(activity!!)
        rv.adapter = bookingsAdapter
        rv.showShimmerAdapter()
    }

    private val carsValueListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Timber.e("Error fetching chats: $p0")
            noCars()
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                hasCars()
            } else {
                noCars()
            }
        }
    }

    private val carsChildListener= object : ChildEventListener {

        override fun onCancelled(p0: DatabaseError) {
            Timber.e("Child listener cancelled: $p0")
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            Timber.e("Chat moved: ${p0.key}")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//            val booking = p0.getValue(Booking::class.java)
//            bookingsAdapter.addBooking(booking!!)
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val booking = p0.getValue(Booking::class.java)
            bookingsAdapter.addBooking(booking!!)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            Timber.e("Chat removed: ${p0.key}")
        }
    }

    private fun hasCars() {
        rv?.hideShimmerAdapter()
        empty?.hideView()
        rv?.showView()
    }

    private fun noCars() {
        rv?.hideShimmerAdapter()
        rv?.hideView()
        empty?.showView()
    }


    override fun onDestroy() {
        super.onDestroy()
        ordersQuery.removeEventListener(carsValueListener)
        ordersQuery.removeEventListener(carsChildListener)
    }

}
