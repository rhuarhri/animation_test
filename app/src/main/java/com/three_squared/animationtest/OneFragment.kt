package com.three_squared.animationtest

import android.os.Bundle
import android.os.Trace
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.fragment.findNavController
import com.three_squared.animationtest.databinding.FragmentOneBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.nio.charset.StandardCharsets
import java.util.*

class OneFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentOneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentOneBinding.inflate(inflater,container, false)

        binding.count = "0"

        val view = binding.root

        val button = binding.fragmentOneBTN
        button.setOnClickListener {
            findNavController().navigate(R.id.action_oneFragment_to_twoFragment, null)
        }

        val popupBTN = binding.viewPopupBTN
        popupBTN.setOnClickListener {
            PopupFragment().show(childFragmentManager, "")
        }

        var count = 0
        val addBTN : Button = binding.addBTN
        addBTN.setOnClickListener {
            count++
            binding.count = count.toString()
        }

        val apiBTN : Button = binding.apiBTN
        apiBTN.setOnClickListener {
            /*val api_key_dev : String = "pvpPZEHs9DqlPP8Bh7GJ0Kk7Uec4dOOS"
            val testBuildConfig : String = BuildConfig.TEST_API_KEY

            val encoded = Base64.getEncoder().encodeToString(api_key_dev.toByteArray())
            println("api key encoded is " + encoded)
            val key = String(android.util.Base64.decode(encoded, android.util.Base64.DEFAULT), StandardCharsets.UTF_8)//Base64.getDecoder().decode(encoded).decodeToString()
            println("test data was $key")
            if (key == api_key_dev) {
                println("decoding successful")
            } else {
                println("failed to decode")
            }*/

            Trace.beginSection("UI blocking")
            UIBlocker().blocker(binding)
            Trace.endSection()
        }

        return view
    }
}


class UIBlocker {
    var count = 0;
    fun blocker(binding: FragmentOneBinding) {
        for (i in 1 .. 10000) {
            binding.count = i.toString()
            //countTXT.setText(count.toString())
        }
    }
}