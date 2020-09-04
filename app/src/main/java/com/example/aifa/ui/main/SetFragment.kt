package com.example.aifa.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.work.*
import com.example.aifa.NotificationWorker

import com.example.aifa.R
import com.example.aifa.databinding.FragmentSetBinding
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var setViewModel: SetViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setViewModel = ViewModelProviders.of(this).get(SetViewModel::class.java)
        val binding: FragmentSetBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_set, container, false)
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val inputs = mapOf(
            "favor" to binding.favor,
            "period" to binding.period,
            "property" to binding.property,
            "money" to binding.money
        )
        val set = mutableMapOf<String, String>()
        inputs.forEach { (name, edit) ->
            edit.doAfterTextChanged {
                set.put(name, edit.text.toString())
            }
        }
        binding.save.setOnClickListener {
            set.forEach { (name, value) ->
                val editor = pref.edit()
                if (pref.getString(name, "") != "") {
                    editor.remove(name)
                }
                editor.putString(name, value).apply()
            }
            //保存计算结果到数据库
        }
        binding.favor.hint = pref.getString("favor", "")
        binding.period.hint = pref.getString("period", "")
        binding.property.hint = pref.getString("property", "")
        binding.money.hint = pref.getString("money", "")
        val dec = DecimalFormat("0.00")
        setViewModel.abs.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.absolute.text = dec.format(it)
                val editor = pref.edit()
                if (pref.getFloat("absolute", 0F) != 0F) {
                    editor.remove("absolute")
                }
                editor.putFloat("absolute", it).apply()
            } else {
                binding.absolute.text = "no fund to sum"
            }
        })
        binding.bond.text = dec.format(pref.getFloat("bond", 0f))
        val manager = WorkManager.getInstance(requireContext())
        setViewModel.liveFunds.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                binding.workButton.text = "Empty"
            } else {
                manager.getWorkInfosForUniqueWorkLiveData("fund")
                    .observe(viewLifecycleOwner, Observer { workInfo ->
                        if (workInfo.size == 0) {
                            binding.workButton.text = "Schedule"
                        } else {
                            binding.workButton.text = workInfo[0].state.toString()
                        }
                    })
            }
        })
        binding.workButton.setOnClickListener { buttonView ->
            if (binding.workButton.text == "Schedule" || binding.workButton.text == "CANCELLED") {
                val network =
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()
                val noticeRequest =
                    PeriodicWorkRequestBuilder<NotificationWorker>(
                        2,
                        TimeUnit.DAYS
                    )
                        .setConstraints(network)
                        //.setInputData(data)
                        .build()
                //manager.cancelAllWork()
                manager.enqueueUniquePeriodicWork(
                    "fund",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    noticeRequest
                )
                //binding.toggleButton.isChecked = true
                Toast.makeText(context, "Enqueue worker", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.workButton.text == "Empty") {
                Toast.makeText(context, "Please add fund to list", Toast.LENGTH_SHORT)
                    .show()
            } else {
                manager.cancelUniqueWork("fund")
                Toast.makeText(context, "Worker Canceled", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.executePendingBindings()
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
