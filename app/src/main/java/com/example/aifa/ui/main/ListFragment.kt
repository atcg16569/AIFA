package com.example.aifa.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.aifa.R
import com.example.aifa.database.Fund
import com.example.aifa.databinding.FragmentListBinding

class ListFragment : Fragment() {

    companion object {
        fun newInstance() = ListFragment()
    }

    private lateinit var listViewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //listViewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        val binding: FragmentListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )
        //binding.fundlist = listViewModel
        val adapter = FundAdapter()
        adapter.listViewModel = listViewModel
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(context)
        listViewModel.fundList.observe(viewLifecycleOwner, { funds ->
            if (adapter.funds.isEmpty()){
                adapter.funds.addAll(funds)
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
                adapter.funds.remove(fund)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }
        val touchHelper = ItemTouchHelper(helper)
        touchHelper.attachToRecyclerView(binding.list)
        // refresh UI for updatetime
        binding.executePendingBindings()
        return binding.root
    }


}
