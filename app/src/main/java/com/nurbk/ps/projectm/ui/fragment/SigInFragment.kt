package com.nurbk.ps.projectm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.FragmentSignInBinding
import com.nurbk.ps.projectm.others.IS_SIGN_IN
import com.nurbk.ps.projectm.others.USER_DATA_PROFILE
import com.nurbk.ps.projectm.ui.dialog.LoadingDialog
import com.nurbk.ps.projectm.ui.viewmodel.SignInAuthViewModel
import com.nurbk.ps.projectm.utils.PreferencesManager

class SigInFragment : Fragment() {

    private lateinit var mBinding: FragmentSignInBinding
    private lateinit var loadingDialog: LoadingDialog

    private val viewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[SignInAuthViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignInBinding.inflate(inflater, container, false)
            .apply { executePendingBindings() }

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mBinding.btnSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_sigInFragment_to_signUpFragment
            )
        }
        loadingDialog = LoadingDialog()
        mBinding.btnSinIn.setOnClickListener {
            val email = mBinding.txtEmail.text.toString()
            val password = mBinding.txtPassword.text.toString()
            loadingDialog.show(requireActivity().supportFragmentManager, "")

            viewModel.signInWithEmailAndPassword(email = email, password = password)

        }

        viewModel.getSignIn().observe(viewLifecycleOwner) {
            val isSignI =
                PreferencesManager(requireContext()).getPreferences()!!.getBoolean(IS_SIGN_IN, false)

            if (it) {
                try {
                    if (isSignI)
                        findNavController().navigate(R.id.action_sigInFragment_to_userListFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (loadingDialog.isAdded)
                loadingDialog.dismiss()
        }



    }

}