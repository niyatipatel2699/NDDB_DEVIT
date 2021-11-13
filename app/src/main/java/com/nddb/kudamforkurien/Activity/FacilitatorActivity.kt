package com.nddb.kudamforkurien.Activity

import android.os.Bundle
import android.view.View
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.databinding.ActivityFacilitatorBinding

class FacilitatorActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val facilitatorbinding = ActivityFacilitatorBinding.inflate(layoutInflater)
        setContentView(facilitatorbinding.root)

        facilitatorbinding.toolbar.tvToolbartitle.setText(R.string.facilitator)
        facilitatorbinding.toolbar.ivHome.setVisibility(View.INVISIBLE)
        facilitatorbinding.toolbar.ivBack.setOnClickListener {
            finish()
        }
    }
}