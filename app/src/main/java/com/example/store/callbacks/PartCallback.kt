package com.example.store.callbacks

import android.view.View
import com.example.store.models.Part

interface PartCallback {

    fun onClick(v: View, part: Part)

}