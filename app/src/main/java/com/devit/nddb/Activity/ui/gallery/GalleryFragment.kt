package com.devit.nddb.Activity.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.devit.nddb.Adapter.gallery_adapter
import com.devit.nddb.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    val list = listOf(
        "https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ54CE_FOgSo3all2YDRz1bJl6D5zrvpZ9vVw&usqp=CAU",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuI22npaeSNOveFW2JEp-IFrSKwJZlMmqsJw&usqp=CAU",
        "https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ54CE_FOgSo3all2YDRz1bJl6D5zrvpZ9vVw&usqp=CAU",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuI22npaeSNOveFW2JEp-IFrSKwJZlMmqsJw&usqp=CAU",
        "https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ54CE_FOgSo3all2YDRz1bJl6D5zrvpZ9vVw&usqp=CAU",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuI22npaeSNOveFW2JEp-IFrSKwJZlMmqsJw&usqp=CAU",
        "https://media.nationalgeographic.org/assets/photos/000/249/24969.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ54CE_FOgSo3all2YDRz1bJl6D5zrvpZ9vVw&usqp=CAU",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuI22npaeSNOveFW2JEp-IFrSKwJZlMmqsJw&usqp=CAU",
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
            GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvPhotos.adapter = lanAdapter

        binding.rvVideos.layoutManager =
            GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvVideos.adapter = lanAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}