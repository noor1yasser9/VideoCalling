package com.nurbk.ps.projectm.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nurbk.ps.projectm.databinding.DialogLoadingBinding

class LoadingDialog : DialogFragment() {

    private lateinit var mBinding: DialogLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogLoadingBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }

        return mBinding.root
    }
}