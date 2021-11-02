package com.devit.nddb.Activity.ui.LanguageList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devit.nddb.Activity.BaseActivity
import com.devit.nddb.Activity.ui.login.LoginActivity
import com.devit.nddb.Adapter.CustomRecyclerAdapter
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.NDDBApp
import com.devit.nddb.R
import com.devit.nddb.data.remote.responses.Language.LanguageData
import com.devit.nddb.databinding.ActivityChooseLanguageBinding
import com.wajahatkarim3.imagine.utils.gone
import com.wajahatkarim3.imagine.utils.showSnack
import com.wajahatkarim3.imagine.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseLanguageActivity : BaseActivity() {

    /* val list = listOf(
         "English",
         "हिंदी",
         "ગુજરાતી",
         "मराठी",
         "தமிழ்", // Tamil
         "తెలుగు", // Telugu
         "కన్నడ",// Kannad
         "മലയാളം",// Malayalam
         "বাংলা",// Bengali
         "ਪੰਜਾਬੀ",// Punjabi
         "অসমীয়া",// Assamese
         "ଓଡିଆ",// Odia
     )*/

    var languageList: ArrayList<LanguageData>? = null
    private val viewModel: LanguageViewModel by viewModels()
    private lateinit var chooseLanguageBinding: ActivityChooseLanguageBinding
    lateinit var lanAdapter : CustomRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chooseLanguageBinding = ActivityChooseLanguageBinding.inflate(layoutInflater)
        setContentView(chooseLanguageBinding.root)

        chooseLanguageBinding.toolbar.tvToolbartitle.setText(R.string.choose_lan)
//        choose_lan_binding.toolbar.conToolMain.setBackgroundResource(R.color.white)
        chooseLanguageBinding.toolbar.ivHome.setVisibility(View.INVISIBLE)
        chooseLanguageBinding.toolbar.ivBack.setVisibility(View.INVISIBLE)

        initObservations()
        viewModel.getLanguage()

        /*  GridLayoutManager(this, 2, RecyclerView.VERTICAL, false).apply {
              chooseLanguageBinding.rvEffectList.layoutManager = this
          }

  //        choose_lan_binding.rvEffectList.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

          var lanAdapter = CustomRecyclerAdapter(this, languageList!!)
          chooseLanguageBinding.rvEffectList.adapter = lanAdapter
  */
        chooseLanguageBinding.btnNext.setOnClickListener {


            if(lanAdapter.row_index==-1)
            {
                chooseLanguageBinding.relChooseLan.showSnack(getString(R.string.select_language))
            }
            else
            {
                var lang_id = languageList?.get(lanAdapter.row_index)
                Log.e("langid-->",lang_id.toString())
                MySharedPreferences.getMySharedPreferences()!!.isLanguageSelected = true
                MySharedPreferences.getMySharedPreferences()!!.lang_id = lang_id!!.id

                // Selected Language
                NDDBApp.getLocaleManager(this)
                    ?.setNewLocale(this, "en");

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("lang_id",lanAdapter.row_index)
                startActivity(intent)
                finish()
            }

     /*       }*/
        }
    }

    private fun initObservations() {

        viewModel.uiStateLiveData.observe(this) { state ->
            when (state) {

                is LoadingState -> {
                    chooseLanguageBinding.progressLanguage.visible()
                }

                is ContentState -> {
                    chooseLanguageBinding.progressLanguage.gone()
                }
                is ErrorState -> {
                    chooseLanguageBinding.relChooseLan.showSnack(state.message)
                    Log.e("error-->", state.message)
                }
            }
        }

        viewModel.lanResponseLiveData.observe(this) { lanResponse ->

            if (lanResponse.status == 1) {
                Log.e("lan-->", lanResponse.items!!.toString())
                languageList = ArrayList()
                languageList?.clear()
                languageList = lanResponse.items!! as ArrayList<LanguageData>
                GridLayoutManager(this, 2, RecyclerView.VERTICAL, false).apply {
                    chooseLanguageBinding.rvEffectList.layoutManager = this
                }
                lanAdapter = CustomRecyclerAdapter(this, languageList!!,1)
                chooseLanguageBinding.rvEffectList.adapter = lanAdapter

            } else {
                lanResponse.message?.let {
                    chooseLanguageBinding.relChooseLan.showSnack(it)
                }
            }
        }
    }
}