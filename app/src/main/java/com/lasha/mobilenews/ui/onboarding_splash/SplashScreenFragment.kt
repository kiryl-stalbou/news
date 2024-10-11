package com.lasha.mobilenews.ui.onboarding_splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentOnboardingSplashBinding
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.util.navigate

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : BaseFragment() {

    private var _binding: FragmentOnboardingSplashBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<SplashScreenViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentOnboardingSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseObservers()
    }

    override fun handleInternetConnection(isAvailable: Boolean) {
        if (isAvailable) {
            viewModel.splashDelay()
            closeErrorBottomSheet()
        } else {
            showErrorBottomSheet(Error.NO_INTERNET_CONNECTION)
        }
    }

    private fun handleListOfDataFromFirebase(listOfDataFromFirebase: MutableList<SubCategoryUiModel>) {
        if (listOfDataFromFirebase.isEmpty()) {
            navigate(R.id.action_splashScreenFragment_to_listInterestsFragment)
        } else {
            navigate(R.id.action_splashScreenFragment_to_articlesMainFragment)
        }
    }

    private fun handleNavigationViewPager() {
        navigate(R.id.action_splashScreenFragment_to_pagerFragment)
    }

    private fun handleDataIsFailed() {
        showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
    }

    private fun initialiseObservers() {
        connectivityLiveData.observe(viewLifecycleOwner, ::handleInternetConnection)
        viewModel.isNavigateToViewPagerNeeded.observe(viewLifecycleOwner) {
            handleNavigationViewPager()
        }
        viewModel.listOfDataFromFirebase.observe(viewLifecycleOwner, ::handleListOfDataFromFirebase)
        viewModel.isDataFailed.observe(viewLifecycleOwner) {
            handleDataIsFailed()
        }
    }
}