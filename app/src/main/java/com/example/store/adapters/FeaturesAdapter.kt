package com.example.store.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.store.R
import com.example.store.commoners.AppUtils.setDrawable
import com.example.store.databinding.ItemFeaturesBinding
import com.example.store.utils.inflate
import com.example.store.utils.setDrawable
import com.mikepenz.ionicons_typeface_library.Ionicons
import timber.log.Timber

class FeaturesAdapter(val context: Context) : RecyclerView.Adapter<FeaturesAdapter.FeaturesHolder>() {
        val features = mutableMapOf<String, Boolean>()

        fun addFeatures(features: MutableMap<String, Boolean>) {
            this.features.putAll(features)
            notifyDataSetChanged()
            Timber.e("Added details")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturesHolder {
            return FeaturesHolder(parent.inflate(R.layout.item_features), context)
        }

        override fun getItemCount(): Int = features.size

        override fun onBindViewHolder(holder: FeaturesHolder, position: Int) {


            holder.bind(features.keys.toList()[position])
        }

        class FeaturesHolder(private val binding: ItemFeaturesBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.tv.setDrawable(setDrawable(context, Ionicons.Icon.ion_android_checkmark_circle, R.color.colorAccent, 18))
            }

            fun bind(feature: String) {

                binding.feature = feature
            }

        }

    }