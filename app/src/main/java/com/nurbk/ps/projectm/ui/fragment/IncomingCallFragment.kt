package com.nurbk.ps.projectm.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.FragmentCallIncomingBinding
import com.nurbk.ps.projectm.model.CallingData
import com.nurbk.ps.projectm.others.CALL_AUDIO
import com.nurbk.ps.projectm.others.TYPE_CALL
import com.nurbk.ps.projectm.others.USER_DATA
import com.nurbk.ps.projectm.ui.activity.MainActivity

class IncomingCallFragment : Fragment() {

    private lateinit var mBinding: FragmentCallIncomingBinding

    private val argumentData by lazy {
        requireArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCallIncomingBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = argumentData.getParcelable<CallingData>(USER_DATA)!!
        mBinding.data = data

        if (data.meetingType == CALL_AUDIO)
            mBinding.imageTypeCall.setImageResource(R.drawable.ic_baseline_call_24)

        mBinding.btnFinshCall.setOnClickListener {
            requireActivity().finish()
        }
    }
}