package com.nddb.kudamforkurien.Activity.ui.Facilitator

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import com.nddb.kudamforkurien.Activity.BaseActivity
import com.nddb.kudamforkurien.Activity.DrawerActivity
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.databinding.ActivityFacilitatorBinding
import com.nddb.kudamforkurien.utils.gone
import com.nddb.kudamforkurien.utils.showSnack
import com.nddb.kudamforkurien.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FacilitatorActivity : BaseActivity() {
    
    private lateinit var facilitatorBinding: ActivityFacilitatorBinding
    private val viewModel: FacilitatorViewModel by viewModels()
    var no_of_people : String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        facilitatorBinding = ActivityFacilitatorBinding.inflate(layoutInflater)
        setContentView(facilitatorBinding.root)

        facilitatorBinding.progressFacilitator.visibility = View.GONE
        facilitatorBinding.toolbar.tvToolbartitle.setText(R.string.facilitator)
        facilitatorBinding.toolbar.ivHome.setVisibility(View.INVISIBLE)
        facilitatorBinding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        initObservations()

        facilitatorBinding.regBtn.setOnClickListener {

            if(facilitatorValidation()){
                viewModel.addFacilitator(no_of_people.toString(),MySharedPreferences.getMySharedPreferences()!!.district)
            }
        }

    }

    private fun facilitatorValidation(): Boolean {

        no_of_people = facilitatorBinding.edtEnterPeople.text.toString()

        return when {
            TextUtils.isEmpty(no_of_people) -> {
                facilitatorBinding.facilitatorRl.showSnack(getString(R.string.please_enter_num_of_people))
                return false
            }
            else -> {
                true
            }
        }
    }

    private fun initObservations() {

        viewModel.uiStateLiveData.observe(this) { state ->

            when(state){
                is LoadingState -> {
                    facilitatorBinding.progressFacilitator.visible()
                }

                is ContentState -> {
                    facilitatorBinding.progressFacilitator.gone()
                }

                is ErrorState -> {
                    facilitatorBinding.progressFacilitator.gone()
                    facilitatorBinding.facilitatorRl.showSnack(state.message)
                }
            }

        }

        viewModel.addFacilitatorLiveData.observe(this){ facilitatorResponse ->

            val status = facilitatorResponse.status
            if(status == 1){
                facilitatorBinding.facilitatorRl.showSnack(facilitatorResponse.message!!)
                val i = Intent(this, DrawerActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            }else{
                facilitatorBinding.facilitatorRl.showSnack(facilitatorResponse.message!!)
            }

        }
    }

}