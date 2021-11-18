package com.nddb.kudamforkurien.Activity.ui.walked_history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nddb.kudamforkurien.Adapter.walked_history_adapter
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.data.remote.responses.History.HistoryData
import com.nddb.kudamforkurien.databinding.WalkedHistoryFragmentBinding
import com.nddb.kudamforkurien.data.room.DatabaseBuilder
import com.nddb.kudamforkurien.data.room.DatabaseHelper
import com.nddb.kudamforkurien.data.room.DatabaseHelperImpl
import com.nddb.kudamforkurien.utils.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalkedHistoryFragment : Fragment() {

    companion object {
        fun newInstance() = WalkedHistoryFragment()
    }

    private lateinit var viewModel: WalkedHistoryViewModel

    private var _binding: WalkedHistoryFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var stepslist:ArrayList<HistoryData>

    private lateinit var lanAdapter:walked_history_adapter
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
        stepslist= ArrayList()

        initObservations()

        var stringResult =
            getString(R.string.your_rank) + " " + MySharedPreferences.getMySharedPreferences()!!.user_rank.toString() + " " + getString(R.string.among_participants)

        binding.tvYourRank.text = stringResult

        binding.tvTotalStep.text = MySharedPreferences.getMySharedPreferences()!!.total_steps.toString()

        /*GlobalScope.launch(Dispatchers.Main) {
            //binding.tvTotalSteps.setText(step.toString())
            dbHelper =
                activity?.let { DatabaseBuilder.getInstance(it) }?.let { DatabaseHelperImpl(it) }!!
            binding.tvTotalStep.text=(dbHelper.totalSteps()).toString()
        }

        GlobalScope.launch (Dispatchers.Main) {
            dbHelper= activity?.let { DatabaseBuilder.getInstance(it) }?.let { DatabaseHelperImpl(it) }!!
            var stepslist=dbHelper.getSteps()

            var total=dbHelper.totalSteps().toString()

            binding.tvTotalStep.text=total

            binding.rvWalkedHistory.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)

            var lanAdapter = walked_history_adapter(requireContext(), stepslist)
            binding.rvWalkedHistory.adapter = lanAdapter
        }
*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initObservations() {
        viewModel.historyResponseLiveData.observe(requireActivity()) { historyResponse ->

            if (historyResponse.status == 1) {

               // stepslist
                stepslist = historyResponse.items as ArrayList<HistoryData>
                binding.rvWalkedHistory.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                lanAdapter = walked_history_adapter(requireContext(), stepslist)
                binding.rvWalkedHistory.adapter = lanAdapter
               // lanAdapter.notifyDataSetChanged()

            } else {
                historyResponse.message?.let {
                    binding.fragmentWalk.showSnack(it)
                }
            }
        }
    }
}