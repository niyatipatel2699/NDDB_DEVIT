package com.devit.nddb.Activity.ui.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devit.nddb.Adapter.slider_adapter
import com.devit.nddb.R
import com.devit.nddb.databinding.EventFragmentBinding
import com.devit.nddb.dialog.AlertDialog
import com.devit.nddb.model.SliderData
import com.smarteist.autoimageslider.SliderView
import android.content.Intent
import com.devit.nddb.Activity.DrawerActivity
import java.util.*
import kotlin.collections.ArrayList


class EventFragment : Fragment() {

    var url1 = "https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg"
    var url2 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ54CE_FOgSo3all2YDRz1bJl6D5zrvpZ9vVw&usqp=CAU"
    var url3 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuI22npaeSNOveFW2JEp-IFrSKwJZlMmqsJw&usqp=CAU"

    companion object {
        fun newInstance() = EventFragment()
    }

    private lateinit var viewModel: EventViewModel

    private var _binding: EventFragmentBinding? = null

    private val binding get() = _binding!!
    lateinit var alertDialog : AlertDialog

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(EventViewModel::class.java)

        _binding = EventFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        alertDialog = AlertDialog(activity)

        binding.tvTotalSteps.setText("0")

//        val stringResult =
//            getString(R.string.your_rank) + " " + 100.toString() + " " + getString(R.string.among_participants)
//        binding.tvYourRank.text = stringResult

        (activity as AppCompatActivity?)!!.supportActionBar!!.title =  getString(R.string.menu_dashboard)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openDialog()

        var sliderDataArrayList: ArrayList<SliderData> = ArrayList()

        sliderDataArrayList.add(SliderData(url1))
        sliderDataArrayList.add(SliderData(url2))
        sliderDataArrayList.add(SliderData(url3))

        val adapter = slider_adapter(requireContext(), sliderDataArrayList)
        binding.slider.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        binding.slider.setSliderAdapter(adapter)
        binding.slider.scrollTimeInSec = 2
        binding.slider.isAutoCycle = true
        binding.slider.startAutoCycle()
    }

    private fun openDialog() {

        alertDialog.btnOk.setOnClickListener{
            alertDialog.dismiss()
            val intent = Intent(requireContext(), DrawerActivity::class.java)
            startActivity(Intent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)))

        }
        alertDialog.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}