package com.lasha.mobilenews.ui.list_interests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentSignInInterestsBinding
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.util.addChips
import com.lasha.mobilenews.util.navigate

class ListInterestsFragment : BaseFragment() {

    private val viewModel by viewModels<ListInterestsViewModel> { viewModelFactory }

    private var _binding: FragmentSignInInterestsBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentSignInInterestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseObservers()
        clickContinueButton()
    }

    private fun checkHowManyCategoriesSaved(listSavedSubCategories: List<SubCategoryUiModel>) {
        binding.continueButton.isEnabled = listSavedSubCategories.size > MINIMUM_INTERESTS_NUMBER
    }

    private fun handleDataIsSaved(isDataToFirebaseSaved: Boolean) {
        if (isDataToFirebaseSaved) {
            navigate(R.id.action_listInterestsFragment_to_successDialogFragment)
        } else {
            showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
        }
    }

    private fun initialiseObservers() {
        viewModel.subCategories.observe(viewLifecycleOwner) { subCategories ->
            addChips(subCategories, binding.chipGroup, layoutInflater) { category ->
                viewModel.onChipClick(category as SubCategoryUiModel)
            }
        }
        viewModel.listSavedSubCategories.observe(
            viewLifecycleOwner,
            ::checkHowManyCategoriesSaved
        )
        viewModel.isDataToFirebaseSaved.observe(viewLifecycleOwner, ::handleDataIsSaved)
    }

    private fun clickContinueButton() {
        binding.continueButton.setOnClickListener {
            if (binding.continueButton.isEnabled) {
                viewModel.saveDataToFirebase()
            }
        }
    }

    companion object {
       private const val MINIMUM_INTERESTS_NUMBER = 2
    }
}