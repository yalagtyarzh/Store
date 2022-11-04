package com.example.store.callbacks

import android.view.View
import com.example.store.models.Car

interface CarCallback {

    fun onClick(v: View, car: Car)

}