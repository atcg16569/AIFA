package com.example.aifa.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aifa.R
import com.example.aifa.Repository
import com.example.aifa.database.Fund
import com.example.aifa.databinding.FundItemBinding
import kotlinx.android.synthetic.main.fund_item.view.*
import java.text.DecimalFormat


class FundAdapter : RecyclerView.Adapter<FundAdapter.FundHolder>() {

    inner class FundHolder(private val itemBinding: FundItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(fund: Fund) {
            val dec = DecimalFormat("0.00")
            itemBinding.name.text = fund.name
            itemBinding.wave.text = dec.format(fund.wave).toString()
            itemBinding.weight.text = dec.format(fund.weight).toString()
            if (fund.status == 0) {
                itemBinding.switch1.text = "pause"
                itemBinding.switch1.isChecked = false
            } else {
                itemBinding.switch1.text = "start"
                itemBinding.switch1.isChecked = true
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
        val binding: FundItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fund_item,
            parent,
            false
        )
        return FundHolder(binding)
    }

    override fun onBindViewHolder(holder: FundHolder, position: Int) {
        val fund = funds[position]
        holder.bind(fund)
        holder.itemView.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fund.status = 1
                Repository.get().updateFund(fund)
            } else {
                fund.status = 0
                Repository.get().updateFund(fund)
            }
        }
        holder.itemView.setOnClickListener {
            if (it.switch1.isVisible) {
                it.switch1.visibility = View.GONE
            } else {
                it.switch1.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = funds.size

    fun getFundAtPosition(position: Int): Fund {
        return funds[position]
    }
}
