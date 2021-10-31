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
import com.wajahatkarim3.imagine.data.room.DatabaseBuilder
import com.wajahatkarim3.imagine.data.room.DatabaseHelper
import com.wajahatkarim3.imagine.data.room.DatabaseHelperImpl
import com.wajahatkarim3.imagine.data.room.entity.Steps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalkedHistoryFragment : Fragment() {



    companion object {
        fun newInstance() = WalkedHistoryFragment()
    }

    private lateinit var viewModel: WalkedHistoryViewModel

    private var _binding: WalkedHistoryFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var stepslist:List<Steps>
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

        GlobalScope.launch (Dispatchers.Main) {
            dbHelper= activity?.let { DatabaseBuilder.getInstance(it) }?.let { DatabaseHelperImpl(it) }!!
            var stepslist=dbHelper.getSteps()

            binding.rvWalkedHistory.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)

            var lanAdapter = walked_history_adapter(requireContext(), stepslist)
            binding.rvWalkedHistory.adapter = lanAdapter
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}