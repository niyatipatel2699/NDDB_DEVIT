package com.nddb.kudamforkurien.Activity

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    private var progressDialog: ProgressDialog? = null
    var b = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanseState: Bundle?
    ): View? {
        return onCreateFragmentView(inflater, parent, savedInstanseState)
    }


    abstract fun onCreateFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View


    open fun showSnackBar(context: Context?, message: String?) {

        (activity as BaseActivity).showSnackBar(context, message)

    }
}


/*
    fun showProgressDialog(ctx: Context?) {

        (activity as BaseActivity).showProgressDialog(ctx!!)
    }

    fun showProgressDialog(ctx: Context?, canCancel: Boolean) {

        (activity as BaseActivity).showProgressDialog(ctx!!)
    }

    fun
        (activity as BaseActivity).hideProgressDialog()
    }

*/

