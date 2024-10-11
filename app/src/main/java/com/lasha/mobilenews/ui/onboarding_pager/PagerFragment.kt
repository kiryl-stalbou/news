package com.lasha.mobilenews.ui.onboarding_pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentOnboardingPagerBinding
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.ViewModelFactory
import com.lasha.mobilenews.util.PageLists
import com.lasha.mobilenews.util.navigate
import javax.inject.Inject

class PagerFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by viewModels<PagerViewModel> { viewModelFactory }

    private var _binding: FragmentOnboardingPagerBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var viewPager: ViewPager2? = null

    private val pagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            initPageInteractions(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingPagerBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).component.inject(this)
        setupViewPager(binding)
        initialiseObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkIsFirstTimeOpened()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager?.unregisterOnPageChangeCallback(pagerCallback)
        _binding = null
    }

    private fun setupViewPager(binding: FragmentOnboardingPagerBinding?) = binding?.run {
        val adapter = PagerAdapter(PageLists(requireContext()).onBoardingList)
        viewPager = onBoardingViewPager
        viewPager?.adapter = adapter
        viewPager?.registerOnPageChangeCallback(pagerCallback)
        viewPager?.let { dotsIndicator.attachTo(it) }
    }

    private fun initPageInteractions(position: Int) = binding.run {
        val pageListSize = PageLists(requireContext()).onBoardingList.indices
        if (position == pageListSize.last) {
            nextPageBtn.text = requireContext().getString(R.string.start)
            nextPageBtn.setOnClickListener {
                navigate(R.id.action_pagerFragment_to_signInFragment)
            }
            skipPagesBtn.visibility = View.INVISIBLE
        } else {
            skipPagesBtn.visibility = View.VISIBLE
            nextPageBtn.text = requireContext().getString(R.string._continue)
            nextPageBtn.setOnClickListener {
                viewPager?.currentItem = position + 1
            }
            skipPagesBtn.setOnClickListener {
                viewPager?.currentItem = pageListSize.last
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = false
            if (viewPager?.currentItem != 0) {
                viewPager?.currentItem = position - 1
            }
        }
    }

    private fun initialiseObservers() {
        viewModel.isFirstLaunch.observe(viewLifecycleOwner, ::checkIsFirstLaunch)
    }

    private fun checkIsFirstLaunch(isFirstLaunch: Boolean) {
        if (isFirstLaunch) {
            navigate(R.id.action_pagerFragment_to_signInFragment)
        }
    }
}