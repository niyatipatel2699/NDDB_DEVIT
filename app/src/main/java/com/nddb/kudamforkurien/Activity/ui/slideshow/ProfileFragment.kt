package com.nddb.kudamforkurien.Activity.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textSlideshow
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        initView()

        return root
    }

    private fun initView() {
       // _binding!!.tvFLName.setText(MySharedPreferences.getMySharedPreferences()!!.first_name + " "+ MySharedPreferences.getMySharedPreferences()!!.last_name)
        _binding!!.fragProfileFname.setText(MySharedPreferences.getMySharedPreferences()!!.first_name)
        _binding!!.fragProfileLname.setText(MySharedPreferences.getMySharedPreferences()!!.last_name)
        _binding!!.fragProfileUtype.setText(MySharedPreferences.getMySharedPreferences()!!.user_type)
        _binding!!.fragProfileState.setText(MySharedPreferences.getMySharedPreferences()!!.state)
        _binding!!.fragmentProfileGenderTv.setText(MySharedPreferences.getMySharedPreferences()!!.gender)
        _binding!!.fragProfileDistrict.setText(MySharedPreferences.getMySharedPreferences()!!.district)
        _binding!!.fragProfileMobile.setText(MySharedPreferences.getMySharedPreferences()!!.phone_number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}