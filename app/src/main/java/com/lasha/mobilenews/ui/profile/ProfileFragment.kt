package com.lasha.mobilenews.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentProfileBinding
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.models.ProfilePageData
import com.lasha.mobilenews.util.loadUrlProfileImage
import com.lasha.mobilenews.util.navigate

class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<ProfileViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).component.inject(this)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickButtons()
        initializeObservers()
    }

    private fun initializeObservers() {
        viewModel.isUserLoggedOut.observe(viewLifecycleOwner) {
            navigateToSignIn()
        }
        viewModel.userData.observe(viewLifecycleOwner, ::setupUserInfoViews)
    }

    private fun navigateToSignIn() {
        navigate(R.id.action_profileFragment_to_signInFragment)
    }

    private fun setupUserInfoViews(profilePageData: ProfilePageData) {
        binding.run {
            profileImage.loadUrlProfileImage(profilePageData.pictureUrl)
            fullNameTv.text = profilePageData.fullName
            emailTv.text = profilePageData.email
        }
    }

    private fun setupClickButtons() {
        binding.run {
            backBtn.setOnClickListener {
                requireActivity().onBackPressed()
            }
            savedArticlesButton.setOnClickListener {
                navigate(R.id.action_profileFragment_to_savedInterestsFragment)
            }
            changeInterestsButton.setOnClickListener {
                navigate(R.id.action_profileFragment_to_myInterestsFragment)
            }
            logoutBtn.setOnClickListener {
                viewModel.logOutUser()
            }
        }
    }
}