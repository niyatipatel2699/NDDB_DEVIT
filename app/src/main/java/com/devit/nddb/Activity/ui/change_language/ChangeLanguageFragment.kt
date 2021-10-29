package com.devit.nddb.Activity.ui.change_language

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devit.nddb.databinding.ChangeLanguageFragmentBinding

class ChangeLanguageFragment : Fragment() {

    private lateinit var changelanguageViewModel: ChangeLanguageViewModel
    private var _binding: ChangeLanguageFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changelanguageViewModel =
            ViewModelProvider(this).get(ChangeLanguageViewModel::class.java)

        _binding = ChangeLanguageFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}