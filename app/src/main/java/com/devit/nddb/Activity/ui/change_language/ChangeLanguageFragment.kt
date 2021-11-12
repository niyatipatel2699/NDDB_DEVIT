package com.devit.nddb.Activity.ui.change_language

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devit.nddb.Activity.DrawerActivity
import com.devit.nddb.Activity.ui.login.LoginActivity
import com.devit.nddb.Adapter.CustomRecyclerAdapter
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.data.remote.responses.Language.LanguageData
import com.devit.nddb.databinding.ChangeLanguageFragmentBinding
import com.wajahatkarim3.imagine.utils.gone
import com.wajahatkarim3.imagine.utils.showSnack
import com.wajahatkarim3.imagine.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatActivity
import com.devit.nddb.Activity.BaseFragment
import com.devit.nddb.NDDBApp


@AndroidEntryPoint
class ChangeLanguageFragment : BaseFragment() {

    val list = listOf(
        "English",
        "हिंदी",
        "ગુજરાતી",
        "मराठी",
        "ਪੰਜਾਬੀ", //=> Punjabi
        "বাংলা ", // bengali
        "தமிழ் ", // tamil
        "தெலுங்கு  ",//telugu
        "ಕನ್ನಡ  ", //kannad
        "മലയാളം ", // malayalam
        "অসমীয়া ", //assamee
        "ରନ୍ " //odiya
    )

    private lateinit var changelanguageViewModel: ChangeLanguageViewModel
    private var _binding: ChangeLanguageFragmentBinding? = null
    var languageList: ArrayList<LanguageData>? = null
    private val viewModel: ChangeLanguageViewModel by viewModels()
    lateinit var lanAdapter : CustomRecyclerAdapter
    var activityContext : FragmentActivity? = null
    var selectedlangid : Int? = null
    var selectedlangName : String? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

  /*  @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changelanguageViewModel =
            ViewModelProvider(this).get(ChangeLanguageViewModel::class.java)

        _binding = ChangeLanguageFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activityContext = activity
        initView()

        (activity as AppCompatActivity?)!!.supportActionBar!!.title =  getString(R.string.choose_lan)

        return root
    }*/

    override fun onCreateFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changelanguageViewModel =
            ViewModelProvider(this).get(ChangeLanguageViewModel::class.java)

        _binding = ChangeLanguageFragmentBinding.inflate(inflater, parent, false)
        val root: View = binding.root
        activityContext = activity
        initView()

        (activity as AppCompatActivity?)!!.supportActionBar!!.title =  getString(R.string.choose_lan)

        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun initView() {


        /*  p0.cardView.setCardBackgroundColor(Color.parseColor("#CA751B"))
            p0.txtTitle.setTextColor(Color.parseColor("#ffffff"))*/

        _binding!!.btnNext.setText(getString(R.string.submit))
        _binding!!.progressLanguage.visibility = View.GONE

        getLanguage()


        _binding!!.btnNext.setOnClickListener {


            if(lanAdapter.row_index==-1)
            {
                _binding!!.changeRel.showSnack(getString(R.string.select_language))
            }
            else
            {
                //var lang_id = languageList?.get(lanAdapter.row_index + 1)
                changelanguageViewModel.updateLanguage(lanAdapter.row_index)
                initObservations()

            }
        }

        //initObservations()
        //viewModel.getLanguage()
    }

    private fun getLanguage() {

        var  gridLayoutManager:GridLayoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
        _binding!!.rvEffectList.layoutManager = gridLayoutManager
        lanAdapter = CustomRecyclerAdapter(requireActivity(), list!!,2)
        _binding!!.rvEffectList.adapter = lanAdapter

        //        choose_lan_binding.rvEffectList.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
    }

    private fun initObservations() {

        activityContext?.let {
            viewModel.lanStateLiveData.observe(it) { state ->
                when (state) {

                    is LoadingState -> {
                        _binding!!.progressLanguage.visible()
                    }

                    is ContentState -> {
                        _binding!!.progressLanguage.gone()
                    }
                    is ErrorState -> {
                        _binding!!.changeRel.showSnack(state.message)
                        Log.e("error-->", state.message)
                    }
                }
            }
        }

      /*  viewModel.lanResponseLiveData.observe(requireActivity()) { lanResponse ->

            if (lanResponse.status == 1) {
                Log.e("lan-->", lanResponse.items!!.toString())
                languageList = ArrayList()
                languageList?.clear()
                languageList = lanResponse.items!! as ArrayList<LanguageData>
               var  gridLayoutManager:GridLayoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
                _binding!!.rvEffectList.layoutManager = gridLayoutManager
                lanAdapter = CustomRecyclerAdapter(requireActivity(), languageList!!,2)
                _binding!!.rvEffectList.adapter = lanAdapter

            } else {
                lanResponse.message?.let {
                    _binding!!.changeRel.showSnack(it)
                }
            }
        }*/

        viewModel.changelanResponseLiveData.observe(requireActivity()) { lanResponse ->

            var updated_lang_id = languageList?.get(lanAdapter.row_index + 1)

            if (lanResponse.status == 1) {
               // _binding!!.changeRel.showSnack(lanResponse.message!!)

                MySharedPreferences.getMySharedPreferences()!!.lang_id = lanAdapter.row_index

                // Selected Language
                if(MySharedPreferences.getMySharedPreferences()!!.lang_id == 0) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "en");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 1) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "hi");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 2) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "gu");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 3) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "mr");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 4) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "pa");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 5) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "bn");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 6) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "ta");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 7) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "te");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 8) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "kn");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 9) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "ml");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 10) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "as");
                }else if (MySharedPreferences.getMySharedPreferences()!!.lang_id == 11) {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "or");
                }else {
                    NDDBApp.getLocaleManager(requireContext())
                        ?.setNewLocale(requireContext(), "en");
                }

                val intent = Intent(requireContext(), DrawerActivity::class.java)
                //showSnackBar(activity,lanResponse.message)
                 Toast.makeText(activityContext,lanResponse.message, Toast.LENGTH_SHORT).show()
                requireContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

//                val intent = Intent(activityContext, DrawerActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)

            } else {
                showSnackBar(activityContext,lanResponse.message)
            }
        }
    }

}