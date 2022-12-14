package com.example.store.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.MenuItem
import com.example.store.R
import com.example.store.commoners.AppUtils
import com.example.store.commoners.BaseActivity
import com.example.store.fragments.OrdersCarsFragment
import com.example.store.fragments.OrdersPartsFragment
import com.example.store.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_orders.*

class OrdersActivity : BaseActivity(), TabLayout.OnTabSelectedListener {

    companion object {
        private const val BOOKINGS = "BOOKINGS"
        private const val PARTS = "PARTS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Orders"

        setupViewPager()
        setupTabs()
    }

    private fun setupViewPager() {
        val adapter = PagerAdapter(supportFragmentManager, this)
        val bookings = OrdersCarsFragment()
        val parts = OrdersPartsFragment()

        adapter.addAllFrags(bookings, parts)
        adapter.addAllTitles(BOOKINGS, PARTS)

        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 1
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

    }

    private fun setupTabs() {
        tabs.setupWithViewPager(viewpager)
        tabs.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewpager.setCurrentItem(tab!!.position, true)
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

}
