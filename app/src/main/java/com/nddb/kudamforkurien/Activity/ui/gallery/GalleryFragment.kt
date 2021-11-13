package com.nddb.kudamforkurien.Activity.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nddb.kudamforkurien.Adapter.gallery_adapter
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.databinding.FragmentGalleryBinding
import com.nddb.kudamforkurien.model.GalleryData

class GalleryFragment : Fragment() {

    var myImageList = intArrayOf(R.drawable.k_10, R.drawable.k_1964_1)

    val list = listOf(
        GalleryData(R.drawable.k_10,R.drawable.k_10,"Image"),
        GalleryData(R.drawable.k_34,R.drawable.k_34,"Image"),
        GalleryData(R.drawable.k_35,R.drawable.k_35,"Video"),
        GalleryData(R.drawable.k_35,R.drawable.k_35,"Video"),
        GalleryData(R.drawable.k_35,R.drawable.k_35,"Image"),
        GalleryData(R.drawable.k_37,R.drawable.k_37,"Image"),
        GalleryData(R.drawable.k_1964_1,R.drawable.k_1964_1,"Image"),
        GalleryData(R.drawable.dr_kurien_with_dr_shankar_1990,R.drawable.dr_kurien_with_dr_shankar_1990,"Image"),
        GalleryData(R.drawable.dr_kurien_with_mr_james_callaghan,R.drawable.dr_kurien_with_mr_james_callaghan,"Image"),
        GalleryData(R.drawable.s_80_1,R.drawable.s_80_1,"Image"),
        GalleryData(R.drawable.ct756,R.drawable.ct756,"Image"),
        GalleryData(R.drawable.ct759,R.drawable.ct759,"Image"),
        //GalleryData(R.drawable.ct768,R.drawable.ct768,"Image"),
        GalleryData(R.drawable.ct599,R.drawable.ct599,"Image"),
        //GalleryData(R.drawable.ct616,R.drawable.ct616,"Image"),
        //GalleryData(R.drawable.ct703,R.drawable.ct703,"Image"),
        GalleryData(R.drawable.ct692,R.drawable.ct692,"Image"),
        GalleryData(R.drawable.ct725,R.drawable.ct725,"Image")
        /*  GalleryData("PtkqwslbLY8","https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg","Video"),
        GalleryData("https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg","https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg","Image"),
        GalleryData("PtkqwslbLY8","https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg","Video")*/

    )

    private var _binding: FragmentGalleryBinding? = null

    private lateinit var viewModel: GalleryViewModel

    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var lanAdapter = gallery_adapter(requireContext(), list)

        binding.rvPhotos.layoutManager =
            GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false)
        binding.rvPhotos.adapter = lanAdapter

//        binding.rvVideos.layoutManager =
//            GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
//        binding.rvVideos.adapter = lanAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}