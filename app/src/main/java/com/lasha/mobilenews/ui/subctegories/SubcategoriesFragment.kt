package com.lasha.mobilenews.ui.subctegories

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentSubcategoriesBinding
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.util.addChips
import com.lasha.mobilenews.util.navigate

class SubcategoriesFragment : BaseFragment() {

    private val viewModel by viewModels<SubcategoriesViewModel> { viewModelFactory }

    private var _binding: FragmentSubcategoriesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val navArgs by navArgs<SubcategoriesFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentSubcategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleNoInternetConnection()
        initialiseObservers()
        setupBackClickListener()
        handleOnBackPressed()
        viewModel.fetchSubCategories(navArgs.category)
    }

    private fun handleOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigate(R.id.action_subcategoriesFragment_to_categoriesFragment)
                }
            })
    }

    private fun handleNoInternetConnection() {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetwork == null) {
            showErrorBottomSheet(Error.NO_INTERNET_CONNECTION)
        }
    }

    private fun initialiseObservers() {
        viewModel.subCategories.observe(viewLifecycleOwner) { subCategories ->
            binding.nothingFoundAnimation.isVisible = subCategories.isEmpty()
            if (subCategories.isNotEmpty()) {
                addChips(subCategories, binding.chipGroup, layoutInflater) { category ->
                    viewModel.onChipClick(category as SubCategoryUiModel)
                }
            }
        }
        viewModel.clickedChip.observe(viewLifecycleOwner, ::navigateToFilteredArticles)
    }

    private fun navigateToFilteredArticles(subCategory: SubCategoryUiModel) {
        val action = SubcategoriesFragmentDirections
            .actionSubcategoriesFragmentToArticleFilteredFragment(subCategory, navArgs.category)
        navigate(action)
    }

    private fun setupBackClickListener() {
        binding.appbar.setNavigationOnClickListener {
            navigate(R.id.action_subcategoriesFragment_to_categoriesFragment)
        }
    }
}