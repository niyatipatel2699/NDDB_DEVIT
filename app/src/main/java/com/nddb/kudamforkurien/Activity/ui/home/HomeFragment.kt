package com.nddb.kudamforkurien.Activity.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nddb.kudamforkurien.Activity.ui.Facilitator.FacilitatorActivity
import com.nddb.kudamforkurien.Adapter.slider_adapter
import com.nddb.kudamforkurien.BuildConfig
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.data.room.DatabaseBuilder
import com.nddb.kudamforkurien.data.room.DatabaseHelper
import com.nddb.kudamforkurien.data.room.DatabaseHelperImpl
import com.nddb.kudamforkurien.data.room.entity.Steps
import com.nddb.kudamforkurien.databinding.FragmentHomeBinding
import com.nddb.kudamforkurien.model.DataSteps
import com.nddb.kudamforkurien.model.SliderData
import com.nddb.kudamforkurien.utils.Converters
import com.nddb.kudamforkurien.utils.NetworkUtils
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


enum class FitActionRequestCode {
    INSERT_AND_READ_DATA,
}

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val dateFormat = DateFormat.getDateTimeInstance()
    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()
    }

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


        dbHelper =
            activity?.let { DatabaseBuilder.getInstance(it) }?.let { DatabaseHelperImpl(it) }!!

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

        var stringResult =
            getString(R.string.your_rank) + " " + MySharedPreferences.getMySharedPreferences()!!.user_rank.toString() + " " + getString(
                R.string.among_participants
            )
        binding.tvYourRank.text = stringResult

        var welcome_text =
            getString(R.string.dear) + " " + MySharedPreferences.getMySharedPreferences()!!.first_name + ", " + getString(
                R.string.you_have
            )
        binding.welcomeText.text = welcome_text

        val date = getCurrentDateTime()
        val dateInString = date.toString("dd-MMM-yyyy HH:mm")

        binding.tvUpdated.text = getString(R.string.updated) + " \n" + dateInString

        if (MySharedPreferences.getMySharedPreferences()!!.is_facilitator == 0) {
            binding.llGotoFacilitator.visibility = View.GONE
        } else {
            binding.llGotoFacilitator.visibility = View.VISIBLE
        }

        binding.verificationBtn.setOnClickListener {
            val intent = Intent(getActivity(), FacilitatorActivity::class.java)
            getActivity()?.startActivity(intent)
        }

//        getRank()
        initObservation()
        return root
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }


    fun sendFitDataToServer() {
        GlobalScope.launch(Dispatchers.Main) {

            var list = dbHelper.getStepsOnlyNotPass()
            if (list.size > 0) {
                var tempList: ArrayList<DataSteps> = ArrayList()
                list.forEach {
                    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                    var outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
                    var inputDateStr = it.date
                    var date: Date = inputFormat.parse(inputDateStr)
                    var parseDate = outputFormat.format(date)
                    var dataSteps = DataSteps(parseDate, it.step!!, it.location.toString())

                    /*{
                        "date": "10 Oct 2021",
                        "steps": 1250,
                        "location": "Ahmedabad"
                    }*/
                    tempList.add(dataSteps)
                }
                val jsonObject = JsonObject()
                val toJson = Gson().toJsonTree(tempList) //Only one line to covert array JsonElement
                jsonObject.add("data", toJson)
                homeViewModel.stepCount(jsonObject)
            }

        }

    }

    fun updateTotalSteps() {

        GlobalScope.launch(Dispatchers.Main) {
            //binding.tvTotalSteps.setText(step.toString())

            var totalSteps = dbHelper.totalSteps()
        }
        sendFitDataToServer()
    }

    fun getRank() {
        homeViewModel.getRank()

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!checkPlayServices()) return
//        getDeviceLocation()
        requestFitPermission()

    }

    fun initObservation() {
        homeViewModel.rankLiveData.observe(requireActivity()) { rankResponse ->
            if (rankResponse.status == 1) {
                //Log.e("rankl",rankResponse.)
                if (rankResponse.items.size > 0) {
                    MySharedPreferences.getMySharedPreferences()?.user_rank =rankResponse.items.get(0).rnk
                    MySharedPreferences.getMySharedPreferences()?.total_steps = rankResponse.items.get(0).total_steps
                    binding.tvContributedSteps.text = rankResponse.items.get(0).total_steps.toString()
                    var totalSteps = rankResponse.items.get(0).total_steps
                }

            } else {
                rankResponse.message?.let { Log.e("error-->", it) }
            }

        }

        homeViewModel.stepCountResponseLiveData.observe(requireActivity()) { stepCountResponse ->
            if (stepCountResponse.status == 1) {
                getRank()
                GlobalScope.launch(Dispatchers.Main) {
                    var list = dbHelper.getStepsOnlyNotPass()
                    list.forEach {
                        if (!it.ispass) {
                            dbHelper.updateSteps(
                                it.id,
                                it.step!!,
                                it.location!!,
                                it.lat!!,
                                it.longitude!!,
                                true
                            )
                        }
                    }
                }

            } else {
                getRank()
                stepCountResponse.message?.let {
                    //  binding.relRegistration.showSnack(it)
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

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
            .setNegativeButton(getString(R.string.label_no)) { dialogInterface, which ->
                dialogInterface.dismiss()
                requestFitPermission()
            }
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
                requestFitPermission()
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
            }
            .setNegativeButton(getString(R.string.label_no)) { dialogInterface, which ->
                mRequestingLocationUpdates = false
                dialogInterface.dismiss()
                requestFitPermission()
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
                requestFitPermission()
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
                requestFitPermission()
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
                requestFitPermission()
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
                            requestFitPermission()
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
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ).not() &&
            grantResults.size == 1 &&
            grantResults[0] == PackageManager.PERMISSION_DENIED
        ) {
//            showSettingsDialog(this)
        } else if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION &&
            permissions.contains(Manifest.permission.ACTIVITY_RECOGNITION) &&
            grantResults.size == 1 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("permission_result", "permission granted")
//            isTrackingStarted = true
            // Google login.
            fitSignIn(FitActionRequestCode.INSERT_AND_READ_DATA)
        }

    }

    private fun requestFitPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION
                )
            } else {
                fitSignIn(FitActionRequestCode.INSERT_AND_READ_DATA)
            }
        } else {
            fitSignIn(FitActionRequestCode.INSERT_AND_READ_DATA)
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
            else if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestFitPermission()
                return
            }
//
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
                        requestFitPermission()
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
                                requestFitPermission()
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


    private fun buildLocationSettingsRequest() {
        if (mLocationSettingsRequest == null) {
            val builder = LocationSettingsRequest.Builder()
            mLocationRequest?.let {
                builder.addLocationRequest(it)
            }
            mLocationSettingsRequest = builder.build()
        }
    }


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


    private fun isPermissionGranted(context: Context, permission: String?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(
            context, permission!!
        ) == PackageManager.PERMISSION_GRANTED
    }


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

//            when (resultCode) {
//                RESULT_OK -> {
//                    if(requestCode == FitActionRequestCode.INSERT_AND_READ_DATA.ordinal){
//                        performActionForRequestCode(FitActionRequestCode.INSERT_AND_READ_DATA)
//                    }
//                }
//                else -> oAuthErrorMsg(requestCode, resultCode)
//            }

            FitActionRequestCode.INSERT_AND_READ_DATA.ordinal -> {
                when (resultCode) {
                    RESULT_OK -> {
                        performActionForRequestCode(FitActionRequestCode.INSERT_AND_READ_DATA)
                    }
                    RESULT_CANCELED -> {
                        oAuthErrorMsg(requestCode, resultCode)
                    }
                }
            }
        }
    }


    private fun getLocationFunction(location: Location) {
        Log.d(TAG, "LOCATION Latitude ${location.latitude} Longitude${location.longitude}")
        MySharedPreferences.getMySharedPreferences()!!.latitude = location.latitude.toString()
        MySharedPreferences.getMySharedPreferences()!!.longitude = location.longitude.toString()
        MySharedPreferences.getMySharedPreferences()!!.location = MySharedPreferences.getMySharedPreferences()!!.district//getCityname(location)
    }


    companion object {


        private const val PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 10001

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


    private fun fitSignIn(requestCode: FitActionRequestCode) {
        if (oAuthPermissionsApproved()) {
            performActionForRequestCode(requestCode)
        } else {
            requestCode.let {
                GoogleSignIn.requestPermissions(
                    this,
                    requestCode.ordinal,
                    getGoogleAccount(), fitnessOptions
                )
            }
        }
    }


    private fun oAuthPermissionsApproved() =
        GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    private fun getGoogleAccount() =
        GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)

    private fun performActionForRequestCode(requestCode: FitActionRequestCode) =
        when (requestCode) {
            FitActionRequestCode.INSERT_AND_READ_DATA -> readHistoryData()
        }

    private fun readHistoryData(): Task<DataReadResponse> {

        Fitness.getRecordingClient(requireActivity(), getGoogleAccount())
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)

        // Begin by creating the query.


        val readRequest = queryFitnessData()

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(requireActivity(), getGoogleAccount())
            .readData(readRequest)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val dataReadResponse = it.result
                    printData(dataReadResponse)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem reading the data.", e)
            }
    }

    private fun queryFitnessData(): DataReadRequest {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        var step: Steps
        var operation = GlobalScope.launch(Dispatchers.Main) {

            step = dbHelper.getLastRow()
            if (step != null) {

                calendar.set(Calendar.DATE, step.date.split("-")[2].toInt())

            } else {
                calendar.set(Calendar.DATE, 0)
            }
        }
        operation.onJoin
        // val calendar = Calendar.getInstance(TimeZone.getDefault())
        // calendar.set(Calendar.DATE, 10)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = Calendar.getInstance(TimeZone.getDefault()).timeInMillis

        /* val calendar = Calendar.getInstance(TimeZone.getDefault())
         calendar.set(Calendar.DATE, 15)
         calendar.set(Calendar.HOUR_OF_DAY, 0)
         calendar.set(Calendar.MINUTE, 0)
         calendar.set(Calendar.SECOND, 0)
         calendar.set(Calendar.MILLISECOND, 0)
         val startTime = calendar.timeInMillis

         val endCalender = Calendar.getInstance(TimeZone.getDefault())
         endCalender.set(Calendar.DATE, 15)
         endCalender.set(Calendar.HOUR_OF_DAY, 23)
         endCalender.set(Calendar.MINUTE, 59)
         endCalender.set(Calendar.SECOND, 0)
         endCalender.set(Calendar.MILLISECOND, 0)
         val endTime = endCalender.timeInMillis*/

        Log.i(TAG, "Range Start: ${dateFormat.format(startTime)}")
        Log.i(TAG, "Range End: ${dateFormat.format(endTime)}")

        val ESTIMATED_STEP_DELTAS: DataSource = DataSource.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .setAppPackageName("com.google.android.gms")
            .build()

        return DataReadRequest.Builder()
            .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .enableServerQueries()
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

    }

    private fun printData(dataReadResult: DataReadResponse) {
        if (dataReadResult.buckets.isNotEmpty()) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                bucket.dataSets.forEach { dumpDataSet(it) }
            }
        } else if (dataReadResult.dataSets.isNotEmpty()) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.dataSets.size)
            dataReadResult.dataSets.forEach { dumpDataSet(it) }
        }
    }

    private fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")

        /*SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj = curFormater.parse(dateStr);

        String newDateStr = curFormater.format(dateObj*/
        // 10  20

        var totalSteps = 0
        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point:")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
            var dateTime = dp.getStartTime(TimeUnit.MILLISECONDS)
            val currentDate = Converters.FORMATTER.format(dateTime)
            dp.dataType.fields.forEach {
                if (it.name == Field.FIELD_STEPS.name) {
                    Log.i(TAG, "\tField: ${it.name} Value: ${dp.getValue(it)}")
                    totalSteps += dp.getValue(it).asInt()
                }
            }

            GlobalScope.launch(Dispatchers.Main) {
                var lat = MySharedPreferences.getMySharedPreferences()!!.latitude
                var lng = MySharedPreferences.getMySharedPreferences()!!.longitude
                var address = MySharedPreferences.getMySharedPreferences()!!.district
                val currentDate = Converters.FORMATTER.format(dateTime)

                var step = dbHelper.getStep(currentDate)
                if (step != null) {
                    dbHelper.updateSteps(step.id, totalSteps, address, lat, lng, false)
                } else {
                    dbHelper.insertSteps(Steps(currentDate, totalSteps, address, lat, lng, false))
                }
                // updateTotalSteps()
            }
        }

        binding.tvTotalSteps.setText(totalSteps.toString())
        binding.circularProgressBar.setProgressWithAnimation(totalSteps.toFloat() / 1500, 1000); // =1s
        updateTotalSteps()
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        Log.e(TAG, message)
    }

    fun DataPoint.getStartTimeString(): String = DateFormat.getTimeInstance()
        .format(this.getStartTime(TimeUnit.MILLISECONDS))

    fun DataPoint.getEndTimeString(): String = DateFormat.getTimeInstance()
        .format(this.getEndTime(TimeUnit.MILLISECONDS))

    fun getCityname(location: Location): String {
        val geocoder = Geocoder(activity, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val cityName: String = addresses[0].getLocality()
        return cityName
        /*  val stateName: String = addresses[0].getAddressLine(1)
          val countryName: String = addresses[0].getAddressLine(2)*/
    }
}