package com.lasha.mobilenews.ui.my_interests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.lasha.mobilenews.databinding.FragmentMyInterestsBinding
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.util.addChips

class MyInterestsFragment : BaseFragment() {

    private val viewModel by viewModels<MyInterestsViewModel> { viewModelFactory }

    private var _binding: FragmentMyInterestsBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentMyInterestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseObservers()
        clickSaveButton()
        setupBackClickListener()
    }

    private fun checkHowManyCategoriesSaved(listSavedCategories: List<SubCategoryUiModel>) {
        binding.saveButton.isEnabled = listSavedCategories.size > MINIMUM_INTERESTS_NUMBER
    }

    private fun checkDataIsSaved(isDataToFirebaseSaved: Boolean) {
        if (isDataToFirebaseSaved) {
            requireActivity().onBackPressed()
        } else {
            showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
        }
    }

    private fun handleDataIsFailed() {
        showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
    }

    private fun initialiseObservers() {
        viewModel.resultSubcategoryList.observe(viewLifecycleOwner) { subCategories ->
            addChips(subCategories, binding.chipGroup, layoutInflater) { category ->
                viewModel.onChipClick(category as SubCategoryUiModel)
            }
        }
        viewModel.listSavedSubCategories.observe(viewLifecycleOwner, ::checkHowManyCategoriesSaved)
        viewModel.isDataToFirebaseSaved.observe(viewLifecycleOwner, ::checkDataIsSaved)
        viewModel.isDataFailed.observe(viewLifecycleOwner)
        {
            handleDataIsFailed()
        }
    }

    private fun setupBackClickListener() {
        binding.appbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun clickSaveButton() {
        binding.saveButton.setOnClickListener {
            if (binding.saveButton.isEnabled) {
                viewModel.saveDataToFirebase()
            }
        }
    }

    companion object {
        private const val MINIMUM_INTERESTS_NUMBER = 2
    }
}