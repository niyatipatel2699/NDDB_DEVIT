package com.devit.nddb.Activity.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.devit.nddb.Activity.FacilitatorActivity
import com.devit.nddb.Adapter.slider_adapter
import com.devit.nddb.BuildConfig
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.backgroundservice.AutoStartService
import com.devit.nddb.backgroundservice.MotionService
import com.devit.nddb.backgroundservice.RestartBroadcastReceiver
import com.devit.nddb.databinding.FragmentHomeBinding
import com.devit.nddb.model.SliderData
import com.devit.nddb.utils.NetworkUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smarteist.autoimageslider.SliderView
import com.wajahatkarim3.imagine.data.room.DatabaseBuilder
import com.wajahatkarim3.imagine.data.room.DatabaseHelper
import com.wajahatkarim3.imagine.data.room.DatabaseHelperImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

  /*  var url1 = R.drawable.app_dashboard_banner_1
    var url2 = R.drawable.app_dashboard_banner_2
    var url3 = R.drawable.app_dashboard_banner_3*/

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    private var mSettingsClient: SettingsClient? = null

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device ha
     */
    private var mLocationSettingsRequest: LocationSettingsRequest? = null


    // Location
    /**
     * Callback for Location events.
     */
    private var mLocationCallback: LocationCallback? = null


    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private var mRequestingLocationUpdates: Boolean = false

    // private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper

    var totalSteps:Int=0
    var isFirstTimeLoad:Boolean=false

    // private val registrationViewModel by viewModels<HomeViewModel>()
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var stringResult =
            getString(R.string.your_rank) + " " + MySharedPreferences.getMySharedPreferences()!!.user_rank.toString() + " " + getString(R.string.among_participants)
        binding.tvYourRank.text = stringResult



//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        /*val stepsNotPass = Database.getInstance(requireActivity()).getEntries()
        if(stepsNotPass.size > 0 )
        {
            homeViewModel.stepCount(stepsNotPass)
        }*/
        GlobalScope.launch(Dispatchers.Main) {
            dbHelper =
                activity?.let { DatabaseBuilder.getInstance(it) }?.let { DatabaseHelperImpl(it) }!!
            var list = dbHelper.getStepsOnlyNotPass()
            if(list.size > 0 )
            {
                homeViewModel.stepCount(list)
            }

        }
        getRank()
        updateTotalSteps()
        /*val overallSteps = Database.getInstance(requireActivity()).getSumSteps(0)
        binding.tvContributedSteps.text=overallSteps.toString()*/
        subscribeService()

        return root
    }

    fun updateTotalSteps()
    {
        isFirstTimeLoad=true
        GlobalScope.launch(Dispatchers.Main) {
            //binding.tvTotalSteps.setText(step.toString())
            dbHelper =
                activity?.let { DatabaseBuilder.getInstance(it) }?.let { DatabaseHelperImpl(it) }!!
            totalSteps=dbHelper.totalSteps()
            binding.tvContributedSteps.text=totalSteps.toString()
        }

    }

    fun getRank(){
        homeViewModel.getRank()
        initObservation()
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        var sliderDataArrayList: ArrayList<SliderData> = ArrayList()

        sliderDataArrayList.add(SliderData(R.drawable.app_dashboard_banner_1))
        sliderDataArrayList.add(SliderData(R.drawable.app_dashboard_banner_2))
        sliderDataArrayList.add(SliderData(R.drawable.app_dashboard_banner_3))

        val adapter = slider_adapter(requireContext(), sliderDataArrayList)
        binding.slider.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        binding.slider.setSliderAdapter(adapter)
        binding.slider.scrollTimeInSec = 10
        binding.slider.isAutoCycle = true
        binding.slider.startAutoCycle()

        /*setupPagerIndidcatorDots()

        ivArrayDotsPager.get(0).setImageResource(R.drawable.page_indicator_selected)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until ivArrayDotsPager.length) {
                    ivArrayDotsPager.get(i).setImageResource(R.drawable.page_indicator_unselected)
                }
                ivArrayDotsPager.get(position).setImageResource(R.drawable.page_indicator_selected)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
*/
        if (MySharedPreferences.getMySharedPreferences()!!.is_facilitator == 0) {
            binding.verificationBtn.visibility = View.GONE
        } else {
            binding.verificationBtn.visibility = View.VISIBLE
        }

        binding.verificationBtn.setOnClickListener {
            val intent = Intent(getActivity(), FacilitatorActivity::class.java)
            getActivity()?.startActivity(intent)
        }

        if (AutoStartService.getInstance() == null) {
            val filter = IntentFilter()
            filter.addAction(AutoStartService.ACTION_FOO)
            val bm = LocalBroadcastManager.getInstance(requireContext())
            bm.registerReceiver(mBroadcastReceiver, filter)

            RestartBroadcastReceiver.scheduleJob(requireContext())
        }

        val prfs: SharedPreferences =
            requireContext().getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE)
        binding.tvTotalSteps.setText(prfs.getString("steps", "0"))

        if (!checkPlayServices()) return
        getDeviceLocation()

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = activity?.packageName
            val pm = activity?.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }*/

        activity?.let {
            homeViewModel.stepCountResponseLiveData.observe(it) { stepCountResponse ->
                if (stepCountResponse.status == 1) {
                    //Log.e("success",loginResponse.message!!)
                   /* val stepsNotPass = Database.getInstance(requireActivity()).getEntries()
                    if(stepsNotPass.size > 0 )
                    {
                        stepsNotPass.forEach {
                            if (!it.ispass) {
                                Database.getInstance(requireActivity()).updateEntry(1)
                                //dbHelper.updateEntry(it.id,it.step!!,it.location!!,it.lat!!,it.longitude!!,true)
                            }
                        }
                    }*/
                    GlobalScope.launch(Dispatchers.Main) {
                        dbHelper = activity?.let { DatabaseBuilder.getInstance(it) }
                            ?.let { DatabaseHelperImpl(it) }!!
                        var list = dbHelper.getStepsOnlyNotPass()
                        list.forEach {
                            if (!it.ispass) {
                                dbHelper.updateSteps(it.id,it.step!!,it.location!!,it.lat!!,it.longitude!!,true)
                            }
                        }
                    }
                } else {
                    stepCountResponse.message?.let {
                        //  binding.relRegistration.showSnack(it)
                    }
                }
            }
        }

    }

    fun initObservation(){

        homeViewModel.rankLiveData.observe(requireActivity()){ rankResponse ->

            if(rankResponse.status == 1){
                //Log.e("rankl",rankResponse.)
                if(rankResponse.items.size>0)
                {
                    MySharedPreferences.getMySharedPreferences()?.user_rank = rankResponse.items.get(0).rnk
                }

            }else {
                rankResponse.message?.let { Log.e("error-->", it) }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val bm = LocalBroadcastManager.getInstance(requireContext())
        bm.unregisterReceiver(mBroadcastReceiver)
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AutoStartService.ACTION_FOO) {
                val param_B = intent.getStringExtra(AutoStartService.EXTRA_PARAM_B)
                if (param_B != null) {
//                    tvTotalSteps.setText("YOUR TOTAL STEPS WALKED -- $param_B")
                    binding.tvTotalSteps.setText(param_B.toString())
//                    Log.e("YOUR TOTAL STEPS WALKED", "$param_B")
                    val preferences: SharedPreferences = context.getSharedPreferences(
                        "AUTHENTICATION_FILE_NAME",
                        Context.MODE_PRIVATE
                    )
                    val editor = preferences.edit()
                    editor.putString("steps", param_B)
                    editor.apply()
                }
            }
        }
    }

    /* Location */

    private fun reDirectToLocationScreen(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_alert))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialogInterface, which ->
                dialogInterface.dismiss()
                openLocationSettingsScreen()
            }
            .setNegativeButton(getString(R.string.label_no)) { dialogInterface, which -> dialogInterface.dismiss() }
            .show()
    }

    private fun askLocationPermission() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_alert))
            .setMessage(getString(R.string.permission_denied_explanation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialogInterface, which ->
                dialogInterface.dismiss()
                mRequestingLocationUpdates = false
                openSettings()
            }
            .setNegativeButton(getString(R.string.label_no)) { dialogInterface, which ->
                mRequestingLocationUpdates = false
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showFlightModeDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_alert))
            .setMessage(getString(R.string.please_disable_flight_mode))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialog, which ->
                dialog.dismiss()
                setFlightMode(requireContext())
                mRequestingLocationUpdates = false
                openSettings()
            }
            .setNegativeButton(getString(R.string.label_no)) { dialogInterface, which ->
                mRequestingLocationUpdates = false
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun locationErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_alert))
            .setMessage(getString(R.string.no_location_detected))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialog, which ->
                dialog.dismiss()
                stopLocationUpdates()
            }
            .show()
    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_alert))
            .setMessage(getString(R.string.permission_rationale))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialog, which ->
                dialog.dismiss()
                startLocationPermissionRequest()
            }
            .setNegativeButton(getString(R.string.label_no)) { dialogInterface, which ->
                mRequestingLocationUpdates = false
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showHighAccuracyDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_alert))
            .setMessage(getString(R.string.high_accuracy_location_not_available))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialog, which ->
                dialog.dismiss()
                openNetworkSettings()
            }
            .setNegativeButton(getString(R.string.label_no)) { dialogInterface, which ->
                mRequestingLocationUpdates = false
                dialogInterface.dismiss()
            }
            .show()
    }

    /**
     * get Device Location
     */
    private fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location")
        if (activity != null) {
            val mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            try {
                val location = mFusedLocationProviderClient.lastLocation
                location.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentLocation = task.result
                        if (currentLocation != null) {
                            //remove previous current location marker and add new one at current position
                            getLocationFunction(currentLocation)
                        } else {
                            requestContinuousLocationUpdates()
                        }
                    } else {
                        Log.d(TAG, "Google Map NearBy Location Complete: current location is null")
                        requestContinuousLocationUpdates()

                    }
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Google Map getDeviceLocation: SecurityException: " + e.message)
                requestContinuousLocationUpdates()
            }
        }
    }

    private fun requestContinuousLocationUpdates() {
        if (mFusedLocationClient == null) {
            mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(
                    requireContext()
                )
        }

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.

        if (mLocationCallback == null) {
            createLocationCallback()
        }
        if (isInFlightMode(requireContext())) {
            showFlightModeDialog()
        } else {
            createLocationRequest()
            buildLocationSettingsRequest()
            checkLocationSettings()
        }
    }


    private fun checkLocationSettings() {
        if (mSettingsClient == null) {
            mSettingsClient = LocationServices.getSettingsClient(requireContext())
        }


        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.

        val locationSettingsResponseTask =
            mSettingsClient?.checkLocationSettings(mLocationSettingsRequest)
        locationSettingsResponseTask?.addOnSuccessListener {
            Log.i(TAG, "All location settings are satisfied.")

            /**
             * Check if Settings->Location is enabled/disabled
             * Not app specific permission (location)
             * Here I am talking of the scenario where Settings->Location is disabled and user runs the app.
             */
            // All location settings are satisfied. The client can initialize location
            afterCheckPermissionRequestLocationUpdates()
        }?.addOnFailureListener { exception ->
            when ((exception as ApiException).statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    mRequestingLocationUpdates = false
                    Log.i(
                        TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings "
                    )
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the
                        // result in onActivityResult().
//                        dialogProvider.hideProgressDialog()
                        val rae = exception as ResolvableApiException
                        rae.startResolutionForResult(
                            requireActivity(),
                            REQUEST_CHECK_SETTINGS
                        )

                    } catch (sie: IntentSender.SendIntentException) {
                        mRequestingLocationUpdates = false
                        Log.i(TAG, "PendingIntent unable to execute request.")
//                        dialogProvider.hideProgressDialog()
                        val errorMessage =
                            getString(R.string.location_setting_cannot_be_fixed_here)
                        Log.e(TAG, errorMessage)
                        reDirectToLocationScreen(errorMessage)
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                    dialogProvider.hideProgressDialog()
                    mRequestingLocationUpdates = false

                    val errorMessage =
                        getString(R.string.location_setting_cannot_be_fixed_here)
                    Log.e(TAG, errorMessage)
                    reDirectToLocationScreen(errorMessage)
                }
            }

        }
    }


    private fun afterCheckPermissionRequestLocationUpdates() {
        if (!checkPermissions()) {
            Log.d(TAG, "Location checkPermissions All permission  Not Granted")
//            dialogProvider.hideProgressDialog()
            requestPermissions()
        } else {
            Log.d(TAG, "Location checkPermissions All permission Granted")
            //getLastLocation(); //from create
            requestLocationUpdates()
        }
    }


    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showPermissionDialog()
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again"
            startLocationPermissionRequest()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
                askLocationPermission()
            }
            // Permission Dialog
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates")
                var granted = true
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        granted = false
                        break
                    }
                }
                if (granted) {
//                    dialogProvider.showProgressDialog(
//                        childFragmentManager,
//                        getString(R.string.fetching_location)
//                    )
                    requestContinuousLocationUpdates()
                } else {
                    askLocationPermission()
                }
            } else {
                // Permission denied.
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.
                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                askLocationPermission()

            }
        }
    }

    private fun requestLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
            else if(ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions()
                return
            }

//            createLocationCallback();
            mRequestingLocationUpdates = true
            if (mFusedLocationClient != null) {
                mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()
                )
            } else {
                stopLocationUpdates()

            }
        } catch (e: Exception) {
            e.printStackTrace()
            mRequestingLocationUpdates = false
            Log.e(TAG, "Lost location permission. Could not request updates. " + e.message)
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)


        var permissionGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionGranted = false
                break
            }
        }
        return permissionGranted
    }

    private fun startLocationPermissionRequest() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION

            ),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }


    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult == null) {
                    locationErrorDialog()
//                    dialogProvider.hideProgressDialog()
//                    dialogProvider.showErrorDialog(childFragmentManager,
//                        getString(R.string.no_location_detected),
//                        getString(
//                            R.string.label_ok
//                        ),
//                        object : OnErrorBtnCallback {
//                            override fun onErrorBtnClicked() {
//                                stopLocationUpdates()
//
//                            }
//                        })
                }



                locationResult?.let { it ->
                    val lastLocation = it.lastLocation
                    if (lastLocation != null) {
//                        dialogProvider.hideProgressDialog()
//                        googleMapRedirectToCurrentLocation(lastLocation)
                        getLocationFunction(lastLocation)
                        stopLocationUpdates()
                        return
                    }

                    val locations = it.locations
                    if (locations.isNotEmpty()) {
                        val lastIndex = locations.lastIndex
                        if (lastIndex != 1) {
                            val newLocation = locations[lastIndex]
                            if (newLocation != null) {
//                                dialogProvider.hideProgressDialog()
//                                googleMapRedirectToCurrentLocation(newLocation)
                                getLocationFunction(newLocation)
                                stopLocationUpdates()
                                return
                            }
                        }

                    }


                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                super.onLocationAvailability(locationAvailability)
//                if(locationAvailability?.isLocationAvailable) {
//                    stopLocationUpdates()
//                showHighAccuracyDialog()
//                }
            }
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     *
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private fun createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest()

            mLocationRequest?.apply {
                // Sets the desired interval for active location updates. This interval is
                // inexact. You may not receive updates at all if no location sources are available, or
                // you may receive them slower than requested. You may also receive updates faster than
                // requested if other applications are requesting location at a faster interval.
                interval = UPDATE_INTERVAL_IN_MILLISECONDS

                // Sets the fastest rate for active location updates. This interval is exact, and your
                // application will never receive updates faster than this value.
                fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }
    }


    /**
     * Uses a [com.google.android.gms.location.LocationSettingsRequest.Builder] to build
     * a [com.google.android.gms.location.LocationSettingsRequest] that is used for checking
     * if a device has the needed location settings.
     */
    private fun buildLocationSettingsRequest() {
        if (mLocationSettingsRequest == null) {
            val builder = LocationSettingsRequest.Builder()
            mLocationRequest?.let {
                builder.addLocationRequest(it)
            }
            mLocationSettingsRequest = builder.build()
        }
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    private fun stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.")
            return
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)?.addOnCompleteListener {
            mRequestingLocationUpdates = false
        }
    }

    private fun checkAllPermissionAndStartLocationUpdate() {
        val permissionAsk = arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,

        )
        val permissionsNotGranted = ArrayList<String>()
        for (permission in permissionAsk) {
            if (!isPermissionGranted(requireContext(), permission)) {
                permissionsNotGranted.add(permission)
            }
        }
        if (permissionsNotGranted.isNotEmpty()) {
            askLocationPermission()
//            showLocationPermissionRejectDialog()
        } else {
//            dialogProvider.showProgressDialog(
//                childFragmentManager,
//                getString(R.string.fetching_location)
//            )
            requestContinuousLocationUpdates()
        }
    }


    /**
     *  check Device is in Flight Mode
     */
    private fun isInFlightMode(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.AIRPLANE_MODE_ON,
                0
            ) != 0
        } else {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON,
                0
            ) != 0
        }
    }


    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityForResult(intent, REQUEST_CHECK_PERMISSION_SETTINGS)
    }


    private fun openLocationSettingsScreen() {
        val locationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(locationIntent, REQUEST_LOCATION)


    }


    /**
     * check Play Service
     *
     * @return
     */
    private fun checkPlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        return if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                val errorDialog =
                    googleApiAvailability.getErrorDialog(requireActivity(), resultCode, 2404)
                if (errorDialog != null) {
                    errorDialog.setCancelable(false)
                    if (!errorDialog.isShowing) {
                        errorDialog.show()
                        errorDialog.setOnDismissListener {
                            if (ConnectionResult.SERVICE_INVALID == resultCode) {
                            } else {
                            }
                        }
                    }
                    false
                } else {
                    false
                }
            } else {
                false
            }
        } else resultCode == ConnectionResult.SUCCESS
    }


    /**
     * In Phone Redirect on FLight Mode screen show user
     * can disable Flight Mode
     *
     * @param context
     */
    fun setFlightMode(context: Context) {
        try {
            // No root permission, just show Airplane / Flight mode setting screen.
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
            //                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, REQUEST_SETTING_CHANGE)
        } catch (e: ActivityNotFoundException) {
            android.util.Log.e(TAG, "Setting screen not found due to: " + e.fillInStackTrace())
            try {
                val intent = Intent("android.settings.WIRELESS_SETTINGS")
                //                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_SETTING_CHANGE)
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
            }
        }
    }

    private fun openNetworkSettings() {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        startActivityForResult(intent, REQUEST_NETWORK_SETTINGS)
    }

    /**
     * @param context    current Context
     * @param permission String permission to ask
     * @return boolean true/false
     */
    private fun isPermissionGranted(context: Context, permission: String?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(
            context, permission!!
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if the provider is enabled of not
     *
     * @param context  any context
     * @param provider the provider to check
     * @return true if the provider is enabled, false otherwise
     */
    private fun isProviderEnabled(context: Context, provider: String): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(provider) ?: false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS, REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(
                        TAG,
                        "User agreed to make required location settings changes."
                    )
//                    dialogProvider.hideProgressDialog()
//                    dialogProvider.showProgressDialog(
//                        childFragmentManager,
//                        getString(R.string.fetching_location)
//                    )
                    requestContinuousLocationUpdates()
                }
                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "User chose not to make required location settings changes.")
                    mRequestingLocationUpdates = false
//                    dialogProvider.hideProgressDialog()
                    // The user was asked to change settings, but chose not to
                    val locationManager =
                        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                    if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                        dialogProvider.hideProgressDialog()
//                        dialogProvider.showProgressDialog(
//                            childFragmentManager,
//                            getString(R.string.fetching_location)
//                        )
                        requestContinuousLocationUpdates()
                    } else {
                        reDirectToLocationScreen(getString(R.string.settings_location_required))
                    }
                }
            }
            REQUEST_SETTING_CHANGE -> {
                Log.i(TAG, "User Request Setting Change.")
                if (!isInFlightMode(requireContext())) {
                    Log.i(TAG, "User Flight mode disable.")
//                    dialogProvider.showProgressDialog(
//                        childFragmentManager,
//                        getString(R.string.fetching_location)
//                    )
                    requestContinuousLocationUpdates()
                } else {
                    Log.i(TAG, "User Flight mode Enable.")
                    mRequestingLocationUpdates = false
                    showFlightModeDialog()
                }
            }

            REQUEST_CHECK_PERMISSION_SETTINGS -> {
                checkAllPermissionAndStartLocationUpdate()
            }
            REQUEST_NETWORK_SETTINGS -> {
                val networkAvailable = NetworkUtils.isNetworkAvailable(requireContext())
//                dialogProvider.hideProgressDialog()

                // The user was asked to change settings, but chose not to
                // The user was asked to change settings, but chose not to
                val locationManager =
                    requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (networkAvailable) {
//                        dialogProvider.showProgressDialog(
//                            childFragmentManager,
//                            getString(R.string.fetching_location)
//                        )
                        requestContinuousLocationUpdates()
                    } else {
//                        dialogProvider.showProgressDialog(
//                            childFragmentManager,
//                            getString(R.string.fetching_location)
//                        )
                        requestContinuousLocationUpdates()
                    }
                } else if (networkAvailable ||
                    !isProviderEnabled(requireContext(), LocationManager.GPS_PROVIDER)
                ) {
//                    dialogProvider.showProgressDialog(
//                        childFragmentManager,
//                        getString(R.string.fetching_location)
//                    )
                    requestContinuousLocationUpdates()
                } else if (!networkAvailable ||
                    !isProviderEnabled(requireContext(), LocationManager.NETWORK_PROVIDER)
                ) {
                    showHighAccuracyDialog()
                }

            }
        }
    }


    private fun getLocationFunction(location: Location) {
        Log.d(TAG, "LOCATION Latitude ${location.latitude} Longitude${location.longitude}")
        MySharedPreferences.getMySharedPreferences()!!.latitude = location.latitude.toString()
        MySharedPreferences.getMySharedPreferences()!!.longitude = location.longitude.toString()
    }


    companion object {


        /**
         * Constant used in the location settings dialog.
         */
        private const val REQUEST_CHECK_SETTINGS = 12345

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Exact. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2


        private const val REQUEST_SETTING_CHANGE = 12346
        private const val REQUEST_CHECK_PERMISSION_SETTINGS = 12348
        private const val REQUEST_NETWORK_SETTINGS = 12347

        // Location
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

        private const val REQUEST_LOCATION = 0x02

        const val TAG = "Home Fragment"
    }

    private fun subscribeService() {
        // start the service and pass a result receiver that is used by the service to update the UI
        val i = Intent(activity, MotionService::class.java)
        i.action = MotionService.ACTION_SUBSCRIBE
        i.putExtra(TAG, object : ResultReceiver(null) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                //Log.e("steps ",resultData.getInt(MotionService.KEY_STEPS).toString())
                if (resultCode == 0) {
                    /*if(isVisible())
                    {

                    }*/
                    Log.e("runOnUiThread",resultData.getInt(MotionService.KEY_STEPS).toString())
                    activity?.runOnUiThread {
                        binding.tvTotalSteps.setText(resultData.getInt(MotionService.KEY_STEPS).toString())
                        //isFirstTimeLoad=false
                        //totalStep(resultData.getInt(MotionService.KEY_STEPS))
                        updateTotalSteps()
                    }
                }
            }
        })
        activity?.startService(i)
    }

    fun totalStep(step:Int)
    {
        if(!isFirstTimeLoad)
        {
            if(step>=totalSteps)
            {
                var tempTotal=step-totalSteps
                totalSteps=totalSteps+tempTotal
            }
            else
            {
                var temp=totalSteps-step
                totalSteps=totalSteps+temp
            }

        }
        isFirstTimeLoad=false
        binding.tvContributedSteps.text=totalSteps.toString()

    }

}