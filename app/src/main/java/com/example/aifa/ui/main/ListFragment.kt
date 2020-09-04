package com.example.aifa.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.aifa.R
import com.example.aifa.database.Out
import com.example.aifa.databinding.FragmentListBinding
import kotlin.math.abs

class ListFragment : Fragment() {

    companion object {
        fun newInstance() = ListFragment()
    }

    private lateinit var listViewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listViewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        val binding: FragmentListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )
        //binding.fundlist = listViewModel
        val adapter = FundAdapter()
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(context)
        listViewModel.fundList.observe(viewLifecycleOwner, Observer { funds ->
            funds.let { fundList ->
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val init = mapOf<String, String?>(
                    "favor" to pref.getString("favor", "0"),
                    "property" to pref.getString("property", "0"),
                    "period" to pref.getString("period", "0"),
                    "money" to pref.getString("money","0")
                )
                val outList = mutableListOf<Out>()
                val abso = pref.getFloat("absolute", 1F)
                if (init.none { it.value == "0" }) {
                    var sumInvest=0.0
                    for (f in fundList) {
                        val part = abs(f.wave) / abso
                        val factor = init["favor"]!!.toFloat() * (1 - f.wave / 100) * part
                        var invest=0.0
                        var everyday=0.0
                        if (f.wave < -5 && f.weight > 0.5) {
                            invest = init["property"]!!.toFloat() * factor * f.weight
                            everyday = invest / init["period"]!!.toInt()
                        }
                        val out = Out(f.id, part, factor, invest, everyday)
                        outList.add(out)
                        sumInvest += invest
                    }
                    val bond=init["property"]!!.toFloat()-init["money"]!!.toFloat()-sumInvest
                    val editor = pref.edit()
                    if (pref.getFloat("bond", 0F) != 0F) {
                        editor.remove("bond")
                    }
                    editor.putFloat("bond", bond.toFloat()).apply()
                }
                adapter.funds = funds//adapter.setFunds(funds)
                adapter.outs = outList
                adapter.notifyDataSetChanged()
            }
        })
        val helper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val fund = adapter.getFundAtPosition(viewHolder.adapterPosition)
                Toast.makeText(context, "delete ${fund.name}", Toast.LENGTH_SHORT).show()
                listViewModel.removeFund(fund)
            }
        }
        val touchHelper = ItemTouchHelper(helper)
        touchHelper.attachToRecyclerView(binding.list)
        // refresh UI for updatetime
        binding.update.text = PreferenceManager.getDefaultSharedPreferences(this.context)
            .getString("update", "UpdateTime")
        binding.executePendingBindings()
        return binding.root
    }


}
