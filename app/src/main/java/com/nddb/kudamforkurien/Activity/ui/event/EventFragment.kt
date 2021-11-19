package com.nddb.kudamforkurien.Activity.ui.event

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nddb.kudamforkurien.Adapter.slider_adapter
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.backgroundservice.MotionService
import com.nddb.kudamforkurien.brodcastreceiver.MyResultReceiver
import com.nddb.kudamforkurien.databinding.EventFragmentBinding
import com.nddb.kudamforkurien.dialog.AlertDialog
import com.nddb.kudamforkurien.model.SliderData
import com.nddb.kudamforkurien.utils.AlarmUtils
import com.nddb.kudamforkurien.utils.Helper
import com.nddb.kudamforkurien.utils.Helper.Companion.runOnUiThread
import com.smarteist.autoimageslider.SliderView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EventFragment : Fragment(),MyResultReceiver.Receiver {
    var TIMER_INTERVAL = 1000
    var url1 = "https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg"
    var url2 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ54CE_FOgSo3all2YDRz1bJl6D5zrvpZ9vVw&usqp=CAU"
    var url3 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuI22npaeSNOveFW2JEp-IFrSKwJZlMmqsJw&usqp=CAU"

    companion object {
        fun newInstance() = EventFragment()
        const val TAG = "Event Fragment"
    }

    private lateinit var viewModel: EventViewModel

    private var _binding: EventFragmentBinding? = null

    private val binding get() = _binding!!
    lateinit var alertDialog: AlertDialog

    var isServiceStart: Boolean = false
    private var mHandler: Handler? = null

    private var timeInSeconds = 0L
    private var startButtonClicked = false


    private var fragmentVisible = false
    lateinit var receiver: MyResultReceiver
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

        fragmentVisible=true


        // alertDialog = AlertDialog(activity)
        var mTodaysSteps =   MySharedPreferences.getMySharedPreferences()!!.keySteps

        binding.tvTotalSteps.setText(mTodaysSteps.toString())
        binding.circularProgressBar.setProgressWithAnimation(mTodaysSteps.toFloat(), 1000); // =1s

//        val stringResult =
//            getString(R.string.your_rank) + " " + 100.toString() + " " + getString(R.string.among_participants)
//        binding.tvYourRank.text = stringResult

        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            getString(R.string.menu_dashboard)

        if(isServiceRunning())
        {
            binding.tvStart.text = activity?.getString(R.string.stop)
            binding.relStartService.setBackgroundResource(R.drawable.stop_ring)
        }

        binding.relStartService.setOnClickListener {

            showAlertDialog()
        }


        if (savedInstanceState != null) {
            receiver = savedInstanceState.getParcelable(TAG)!!
        } else {
            receiver = MyResultReceiver(Handler())
        }
        receiver.setReceiver(this)

        return root
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(TAG, receiver)
        super.onSaveInstanceState(outState)
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
                    //startTimer()
                    binding.tvStart.text = activity?.getString(R.string.stop)
                    binding.relStartService.setBackgroundResource(R.drawable.stop_ring)
                    startAlarm()
                } else {
                   /* stopTimer()
                    resetTimerView()*/
                    val intent = Intent(activity, MotionService::class.java)
                    intent.putExtra("stopped", true)
                    //activity?.startService(intent)
                    ContextCompat.startForegroundService(requireContext(), intent)
                    binding.relStartService.setBackgroundResource(R.drawable.start_ring)
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
        sliderDataArrayList.add(SliderData(R.drawable.app_dashboard_banner_4))
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
        fragmentVisible=false
        //resetTimerView()
    }



    private fun subscribeService() {
        // start the service and pass a result receiver that is used by the service to update the UI
       // val receiver: ResultReceiver = MyResultReceiver(null)

        val intent = Intent(activity, MotionService::class.java)
        intent.putExtra(TAG, receiver)
        intent.action = MotionService.ACTION_SUBSCRIBE
        activity?.startService(intent)

      /*  val i = Intent(activity, MotionService::class.java)
        i.action = MotionService.ACTION_SUBSCRIBE
        i.putExtra(EventFragment.TAG, object : ResultReceiver(null) {
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
                else if(resultCode==1)
                {
                    activity?.runOnUiThread {
                        //resultData.getInt(MotionService.KEY_STEPS)
                        binding?.textViewStopWatch?.text= resultData.getString(MotionService.KEY_TIMER)
                    }

                }
            }
        })
        activity?.startService(i)*/
        isServiceStart = true
    }


    fun startAlarm()
    {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.MINUTE, 10);
        val alarmUtils = AlarmUtils(requireContext())
        alarmUtils.initRepeatingAlarm(calendar)
    }

    override fun onReceiveResult(command: Int, resultData: Bundle?) {
       Log.e("Fafasfasf",""+command+""+resultData)

        if(!isVisible)
            return

        if (command == 0) {
            Log.e("runOnUiThread", resultData?.getInt(MotionService.KEY_STEPS).toString())
            runOnUiThread {
                //resultData.getInt(MotionService.KEY_STEPS)
                binding.tvTotalSteps.setText(
                    resultData?.getInt(MotionService.KEY_STEPS).toString()
                )
                binding.circularProgressBar.setProgressWithAnimation(resultData?.getInt(MotionService.KEY_STEPS)!!.toFloat(), 1000); // =1s

            }
        }
        else if(command==1)
        {
            runOnUiThread {
                    binding?.textViewStopWatch?.text= resultData?.getString(MotionService.KEY_TIMER)
            }

        }
    }


    /* private fun resetTimerView() {
         timeInSeconds = 0
         startButtonClicked = false
     }

     private fun startTimer() {
         mHandler = Handler(Looper.getMainLooper())
         mStatusChecker.run()
     }

     private fun stopTimer() {
         mHandler?.removeCallbacks(mStatusChecker)
     }

     private var mStatusChecker: Runnable = object : Runnable {
         override fun run() {
             try {
                 timeInSeconds += 1
                 Log.e("timeInSeconds", timeInSeconds.toString())
                 updateStopWatchView(timeInSeconds)
             } finally {
                 // 100% guarantee that this always happens, even if
                 // your update method throws an exception
                 mHandler!!.postDelayed(this, TIMER_INTERVAL.toLong())
             }
         }
     }

     private fun updateStopWatchView(timeInSeconds: Long) {
         val formattedTime = getFormattedStopWatch((timeInSeconds * 1000))
         Log.e("formattedTime", formattedTime)
         binding?.textViewStopWatch?.text = formattedTime
     }


     fun getFormattedStopWatch(ms: Long): String {
         var milliseconds = ms
         val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
         milliseconds -= TimeUnit.HOURS.toMillis(hours)
         val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
         milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
         val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

         return "${if (hours < 10) "0" else ""}$hours:" +
                 "${if (minutes < 10) "0" else ""}$minutes:" +
                 "${if (seconds < 10) "0" else ""}$seconds"
     }*/

}