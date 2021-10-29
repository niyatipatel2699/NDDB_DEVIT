package com.devit.nddb.Activity.ui.walked_history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devit.nddb.Adapter.walked_history_adapter
import com.devit.nddb.databinding.WalkedHistoryFragmentBinding

class WalkedHistoryFragment : Fragment() {

    val list = listOf(
        "100",
        "2000",
        "3000",
        "4000",
        "5000",
        "6000",
        "7000",
        "8000",
        "9000",
        "10000",
        "11000",
        "12000",
    )

    companion object {
        fun newInstance() = WalkedHistoryFragment()
    }

    private lateinit var viewModel: WalkedHistoryViewModel

    private var _binding: WalkedHistoryFragmentBinding? = null

    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(WalkedHistoryViewModel::class.java)

        _binding = WalkedHistoryFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvWalkedHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)

        var lanAdapter = walked_history_adapter(requireContext(), list)
        binding.rvWalkedHistory.adapter = lanAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}