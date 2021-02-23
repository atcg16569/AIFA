package com.example.aifa.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.aifa.NotificationWorker

import com.example.aifa.R
import com.example.aifa.databinding.FragmentSetBinding
import java.util.concurrent.TimeUnit

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetFragment : Fragment() {
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
    ): View {
        // Inflate the layout for this fragment
        setViewModel = ViewModelProvider(this).get(SetViewModel::class.java)
        val binding: FragmentSetBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_set, container, false)
        val manager = WorkManager.getInstance(requireContext())
        setViewModel.liveFunds.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.workButton.text = "Empty"
            } else {
                manager.getWorkInfosForUniqueWorkLiveData("fund")
                    .observe(viewLifecycleOwner, { workInfo ->
                        if (workInfo.size == 0) {
                            binding.workButton.text = "Schedule"
                        } else {
                            binding.workButton.text = workInfo[0].state.toString()
                        }
                    })
            }
        })
        binding.workButton.setOnClickListener {
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
