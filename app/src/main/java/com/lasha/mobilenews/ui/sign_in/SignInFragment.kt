package com.lasha.mobilenews.ui.sign_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentSignInBinding
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.util.navigate
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInFragment : BaseFragment() {

    private lateinit var launcher: ActivityResultLauncher<Intent>

    @Inject
    lateinit var gso: GoogleSignInOptions

    private val viewModel by viewModels<SignInViewModel> { viewModelFactory }

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerUser()
        initialiseObservers()
        onSignInButtonClick()
        handleOnBackPressed()
    }

    private fun handleNavigationToToBottomSheet() {
        showErrorBottomSheet(Error.ERROR_WITH_AUTHENTICATION)
    }

    private fun handleOnBackPressed() {
        activity
            ?.onBackPressedDispatcher
            ?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.moveTaskToBack(true)
                    activity?.finish()
                }
            })
    }

    private fun handleListOfDataFromFirebase(listOfDataFromFirebase: List<SubCategoryUiModel>) {
        if (listOfDataFromFirebase.isEmpty()) {
            navigate(R.id.action_signInFragment_to_listInterestsFragment)
        } else {
            navigate(R.id.action_signInFragment_to_articlesMainFragment)
        }
    }

    private fun handleDataIsFailed() {
        showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
    }

    private fun initialiseObservers() {
        viewModel.isNavigateToBottomSheetNeeded.observe(viewLifecycleOwner) {
            handleNavigationToToBottomSheet()
        }
        viewModel.listOfDataFromFirebase.observe(viewLifecycleOwner, ::handleListOfDataFromFirebase)
        viewModel.isDataFailed.observe(viewLifecycleOwner) {
            handleDataIsFailed()
        }
    }

    private fun registerUser() {
        lifecycleScope.launch {
            launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        if (account != null) {
                            viewModel.firebaseAuthWithGoogle(
                                account.idToken.toString()
                            )
                        }
                    } catch (e: ApiException) {
                        Log.d("ApiException", "User canceled login")
                    }
                }
        }
    }

    private fun onSignInButtonClick() {
        binding.signInImageButton.setOnClickListener {
            singInWithGoogle()
        }
    }

    private fun singInWithGoogle() {
        val signInClient = GoogleSignIn.getClient(requireActivity(), gso)
        launcher.launch(signInClient.signInIntent)
    }
}