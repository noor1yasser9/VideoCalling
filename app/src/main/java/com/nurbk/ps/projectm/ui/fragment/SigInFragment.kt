package com.nurbk.ps.projectm.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.FragmentSignInBinding
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.others.IS_SIGN_IN
import com.nurbk.ps.projectm.ui.dialog.LoadingDialog
import com.nurbk.ps.projectm.ui.viewmodel.SignInAuthViewModel
import com.nurbk.ps.projectm.utils.PreferencesManager
import java.util.*


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
                PreferencesManager(requireContext()).getPreferences()!!.getBoolean(
                    IS_SIGN_IN,
                    false
                )

            if (it) {
                try {
                    if (isSignI)
                        findNavController().navigate(R.id.action_users_list_fragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (loadingDialog.isAdded)
                loadingDialog.dismiss()
        }

        if (PreferencesManager(requireContext())
                .getPreferences()
            !!.getBoolean(IS_SIGN_IN, false) && FirebaseAuth.getInstance().currentUser != null
        ) {
            findNavController().navigate(R.id.action_users_list_fragment)
        }


        callbackManager = CallbackManager.Factory.create();
        mBinding.loginButton.setOnClickListener {
            FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
            LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList("public_profile")
            );

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        // App code
                        val batch = GraphRequestBatch(
                            GraphRequest.newMeRequest(
                                loginResult!!.accessToken
                            ) { jsonObject, response ->

                                handleFacebookAccessToken(loginResult.accessToken) {
                                    viewModel.insertUsers(
                                        User(
                                            FirebaseAuth.getInstance().uid.toString(),
                                            jsonObject.getString("name")
                                        )
                                    )
                                    findNavController().navigate(R.id.action_users_list_fragment)
                                }
                            },
                            GraphRequest.newMyFriendsRequest(
                                loginResult.accessToken
                            ) { jsonArray, response -> }
                        )

                        batch.executeAsync()
                    }

                    override fun onCancel() = Unit

                    override fun onError(exception: FacebookException) = Unit
                })
        }

        mBinding.googleSignIn.setOnClickListener {

        }
    }


    private var callbackManager: CallbackManager? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }


    private fun handleFacebookAccessToken(token: AccessToken, onComplete: () -> Unit) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete()
                } else {

                }

            }
    }
}