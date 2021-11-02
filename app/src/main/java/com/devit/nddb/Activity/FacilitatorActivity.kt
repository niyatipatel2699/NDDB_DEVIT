package com.devit.nddb.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.devit.nddb.R
import com.devit.nddb.databinding.ActivityFacilitatorBinding

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