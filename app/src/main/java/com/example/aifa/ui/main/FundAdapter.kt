package com.example.aifa.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.aifa.database.Fund
import com.example.aifa.databinding.FundItemBinding
import java.text.DecimalFormat


class FundAdapter : RecyclerView.Adapter<FundAdapter.FundHolder>() {

    inner class FundHolder(private val itemBinding: FundItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(fund: Fund,listViewModel: ListViewModel) {
            val dec = DecimalFormat("0.00")
            itemBinding.name.text = fund.name
            itemBinding.wave.text = dec.format(fund.wave).toString()
            itemBinding.weight.text = dec.format(fund.weight).toString()
            if (fund.wave < -5 && fund.weight > 0.5) {
                itemBinding.value.setBackgroundColor(Color.parseColor("#55bb8a"))
            }
            val switch = itemBinding.switch1
            if (fund.status == 0) {
                switch.text = "pause"
                switch.isChecked = false
            } else {
                switch.text = "start"
                switch.isChecked = true
            }
            itemBinding.table.setOnClickListener {
                if (switch.isVisible) {
                    switch.visibility = View.GONE
                } else {
                    switch.visibility = View.VISIBLE
                }
            }
            switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    fund.status = 1
                    notifyItemChanged(adapterPosition,fund)
                    listViewModel.updateFund(fund)
                } else {
                    fund.status = 0
                    notifyItemChanged(adapterPosition,fund)
                    listViewModel.updateFund(fund)
                }
            }
        }
    }


    val funds = mutableListOf<Fund>()
    lateinit var listViewModel:ListViewModel
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
        holder.bind(funds[position],listViewModel)
    }

    override fun getItemCount() = funds.size

    fun getFundAtPosition(position: Int): Fund {
        return funds[position]
    }
}
