package com.example.aifa.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aifa.R
import com.example.aifa.database.Fund
import com.example.aifa.database.Out
import com.example.aifa.databinding.FundItemBinding
import java.text.DecimalFormat


class FundAdapter : RecyclerView.Adapter<FundAdapter.FundHolder>() {

    inner class FundHolder(private val itemBinding: FundItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(fund: Fund, out: Out = Out("consent", 0.0, 0.0, 0.0, 0.0)) {
            val dec = DecimalFormat("0.00")
            itemBinding.part.text = dec.format(out.part).toString()
            itemBinding.factor.text = dec.format(out.factor).toString()
            itemBinding.invest.text = dec.format(out.invest).toString()
            itemBinding.everyday.text = dec.format(out.everyday).toString()
            itemBinding.name.text = fund.name
            itemBinding.today.text = dec.format(fund.today).toString()
            itemBinding.lowest.text = dec.format(fund.lowest).toString()
            itemBinding.highest.text = dec.format(fund.highest).toString()
            itemBinding.month1.text = dec.format(fund.month1).toString()
            itemBinding.month3.text = dec.format(fund.month3).toString()
            itemBinding.month6.text = dec.format(fund.month6).toString()
            itemBinding.wave.text = dec.format(fund.wave).toString()
            itemBinding.past.text = dec.format(fund.past).toString()
            itemBinding.present.text = dec.format(fund.present).toString()
            itemBinding.weight.text = dec.format(fund.weight).toString()
        }
    }


    var funds = listOf<Fund>()
    var outs = mutableListOf<Out>()
    internal fun setFunds(funds: List<Fund>) {
        this.funds = funds
        notifyDataSetChanged()
    }

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
        if(outs.isEmpty()){
            holder.bind(fund)
        }else{
            val out = outs[position]
            holder.bind(fund, out)
        }
    }

    override fun getItemCount() = funds.size

    fun getFundAtPosition(position: Int): Fund {
        return funds.get(position)
    }
}
