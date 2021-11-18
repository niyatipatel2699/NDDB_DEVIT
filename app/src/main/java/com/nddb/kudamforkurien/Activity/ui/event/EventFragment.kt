package com.nddb.kudamforkurien.Activity.ui.event

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.ResultReceiver
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nddb.kudamforkurien.Activity.ui.home.HomeFragment
import com.nddb.kudamforkurien.Activity.ui.login.LoginActivity
import com.nddb.kudamforkurien.Adapter.slider_adapter
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.backgroundservice.MotionService
import com.nddb.kudamforkurien.backgroundservice.ServiceAdmin
import com.nddb.kudamforkurien.databinding.EventFragmentBinding
import com.nddb.kudamforkurien.dialog.AlertDialog
import com.nddb.kudamforkurien.model.SliderData
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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
    lateinit var alertDialog: AlertDialog

    var isServiceStart: Boolean = false

    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // alertDialog = AlertDialog(activity)
        var mTodaysSteps = sharedPreferences.getInt(MotionService.KEY_STEPS, 0)

        binding.tvTotalSteps.setText(mTodaysSteps.toString())

//        val stringResult =
//            getString(R.string.your_rank) + " " + 100.toString() + " " + getString(R.string.among_participants)
//        binding.tvYourRank.text = stringResult

        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            getString(R.string.menu_dashboard)

        if(isServiceRunning())
        {
            binding.tvStart.text = activity?.getString(R.string.stop)
        }

        binding.relStartService.setOnClickListener {

            showAlertDialog()
        }

        return root
    }

    private fun showAlertDialog() {
        var alertMessage = getString(R.string.are_you_sure_want_stop_event)
        if(!isServiceRunning())
        { alertMessage = getString(R.string.are_you_sure_want_start_event) }
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage(alertMessage)
            .setPositiveButton(
                getString(R.string.label_yes)
            ) { dialogInterface, i ->
                dialogInterface.dismiss()
                if (!isServiceRunning()) {
                    subscribeService()
                    binding.tvStart.text = activity?.getString(R.string.stop)
                } else {
                    val intent = Intent(activity, MotionService::class.java)
                    intent.putExtra("stopped", true)
                    //activity?.startService(intent)
                    ContextCompat.startForegroundService(requireContext(), intent)

                   // isServiceStart = false
                    binding.tvStart.text = activity?.getString(R.string.start)
                }
            }
            .setNegativeButton(
                getString(R.string.label_no)
            ) { dialogInterface, i -> dialogInterface.dismiss() }.show()

    }

    private fun isServiceRunning(): Boolean {
        val manager = activity?.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if ("com.nddb.kudamforkurien.backgroundservice.MotionService" == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ///openDialog()
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        val finalDate = "26-11-2021"


        /*if (finalDate.compareTo(currentDate) > 0)
        {      alertDialog.show()}
        else if (finalDate.compareTo(currentDate) < 0)
        {   alertDialog.dismiss()}
        else if (finalDate.compareTo(currentDate) == 0)
        {    alertDialog.dismiss()}

        alertDialog.btnOk.setOnClickListener{
            alertDialog.dismiss()
            val intent = Intent(requireContext(), DrawerActivity::class.java)
            startActivity(Intent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)))
        }*/

        if (finalDate.compareTo(currentDate) > 0) {     // alertDialog.show()}
            binding.imageRl.visibility = View.GONE
            binding.mainRl.visibility = View.VISIBLE
        } else if (finalDate.compareTo(currentDate) < 0) {   /*alertDialog.dismiss()*/
            binding.imageRl.visibility = View.VISIBLE
            binding.mainRl.visibility = View.GONE
        } else if (finalDate.compareTo(currentDate) == 0)
        /*{    alertDialog.dismiss()*/ {
            binding.imageRl.visibility = View.VISIBLE
            binding.mainRl.visibility = View.GONE
        }

        var sliderDataArrayList: ArrayList<SliderData> = ArrayList()

        /* sliderDataArrayList.add(SliderData(url1))
         sliderDataArrayList.add(SliderData(url2))
         sliderDataArrayList.add(SliderData(url3))*/

        sliderDataArrayList.add(SliderData(R.drawable.app_dashboard_banner_1))
        sliderDataArrayList.add(SliderData(R.drawable.app_dashboard_banner_2))
        sliderDataArrayList.add(SliderData(R.drawable.app_dashboard_banner_3))


        val adapter = slider_adapter(requireContext(), sliderDataArrayList)
        binding.slider.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        binding.slider.setSliderAdapter(adapter)
        binding.slider.scrollTimeInSec = 2
        binding.slider.isAutoCycle = true
        binding.slider.startAutoCycle()
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

    private fun subscribeService() {
        // start the service and pass a result receiver that is used by the service to update the UI
        val i = Intent(activity, MotionService::class.java)
        i.action = MotionService.ACTION_SUBSCRIBE
        i.putExtra(HomeFragment.TAG, object : ResultReceiver(null) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                //Log.e("steps ",resultData.getInt(MotionService.KEY_STEPS).toString())
                if (resultCode == 0) {
                    Log.e("runOnUiThread", resultData.getInt(MotionService.KEY_STEPS).toString())
                    activity?.runOnUiThread {
                        //resultData.getInt(MotionService.KEY_STEPS)
                        binding.tvTotalSteps.setText(
                            resultData.getInt(MotionService.KEY_STEPS).toString()
                        )
                        binding.circularProgressBar.setProgressWithAnimation(resultData.getInt(MotionService.KEY_STEPS).toFloat(), 1000); // =1s
                        //isFirstTimeLoad=false
                        //totalStep(resultData.getInt(MotionService.KEY_STEPS))
                        //updateTotalSteps()
                    }
                }
            }
        })
        activity?.startService(i)
        isServiceStart = true
    }

}