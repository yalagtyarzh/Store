package com.example.store.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.store.R
import com.example.store.commoners.BaseActivity
import com.example.store.fragments.LoginFragment
import com.example.store.fragments.RegisterFragment
import com.example.store.utils.addFragment
import org.jetbrains.anko.toast

class AuthActivity : BaseActivity() {
    private lateinit var loginFragment: LoginFragment
    private lateinit var registerFragment: RegisterFragment
    private var doubleBackToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        checkIfLoggedIn()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        loginFragment = LoginFragment()
        registerFragment = RegisterFragment()

        addFragment(loginFragment, R.id.authHolder)
    }

    private fun checkIfLoggedIn() {
        if (getUser() != null){
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0,0)
            finish()
        }
    }

    override fun onBackPressed() {
        if (!registerFragment.backPressOkay() || !loginFragment.backPressOkay()) {
            toast("Please wait...")

        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            if (doubleBackToExit) {
                super.onBackPressed()
            } else {
                toast("Tap back again to exit")
                doubleBackToExit = true

                Handler().postDelayed({doubleBackToExit = false}, 1500)
            }
        }

    }

}
