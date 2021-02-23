package com.example.aifa.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.aifa.Repository
import com.example.aifa.database.Fund
import com.example.aifa.databinding.FundItemBinding
import java.text.DecimalFormat


class FundAdapter : RecyclerView.Adapter<FundAdapter.FundHolder>() {

    inner class FundHolder(private val itemBinding: FundItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(fund: Fund) {
            val dec = DecimalFormat("0.00")
            itemBinding.name.text = fund.name
            itemBinding.wave.text = dec.format(fund.wave).toString()
            itemBinding.weight.text = dec.format(fund.weight).toString()
            val switch=itemBinding.switch1
            if (fund.status == 0) {
                switch.text = "pause"
                switch.isChecked = false
            } else {
                switch.text = "start"
                switch.isChecked = true
            }
            itemBinding.root.setOnClickListener {
                if (switch.isVisible) {
                    switch.visibility = View.GONE
                } else {
                    switch.visibility = View.VISIBLE
                }
            }
            switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    fund.status = 1
                    Repository.get().updateFund(fund)
                } else {
                    fund.status = 0
                    Repository.get().updateFund(fund)
                }
            }
        }
    }


    var funds = listOf<Fund>()
    /*internal fun setFunds(funds: List<Fund>) {
        this.funds = funds
        notifyDataSetChanged()
    }
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundHolder {
        val binding: FundItemBinding = FundItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FundHolder(binding)
    }

    override fun onBindViewHolder(holder: FundHolder, position: Int) {
        val fund = funds[position]
        holder.bind(fund)
    }

    override fun getItemCount() = funds.size

    fun getFundAtPosition(position: Int): Fund {
        return funds[position]
    }
}
