package com.nurbk.ps.projectm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.FragmentSignInBinding
import com.nurbk.ps.projectm.dialog
import com.nurbk.ps.projectm.showDialog
import com.nurbk.ps.projectm.ui.viewmodel.SignInAuthViewModel

class SigInFragment : Fragment() {

    private lateinit var mBinding: FragmentSignInBinding
    private val viewModel by lazy {
        ViewModelProvider(
            this,
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

        mBinding.btnSinIn.setOnClickListener {
            val email = mBinding.txtEmail.text.toString()
            val password = mBinding.txtPassword.text.toString()
            showDialog(requireActivity())
            viewModel.signInWithEmailAndPassword(email = email, password = password)

        }

        viewModel.getSignIn().observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "True", Toast.LENGTH_LONG).show()
            }
            dialog.hide()
        }

    }

}