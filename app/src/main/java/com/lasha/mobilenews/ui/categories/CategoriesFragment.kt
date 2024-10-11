package com.lasha.mobilenews.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentCategoriesBinding
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.models.MainCategoryUiModel
import com.lasha.mobilenews.util.addChips
import com.lasha.mobilenews.util.loadUrlProfileImage
import com.lasha.mobilenews.util.navigate

class CategoriesFragment : BaseFragment() {

    private val viewModel by viewModels<CategoriesViewModel> { viewModelFactory }

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseObservers()
        setupClickListeners()
        showBottomNavigation()
        handleOnBackPressed()
    }

    private fun setupClickListeners() {
        binding.run {
            circleIvAppbar.setOnClickListener {
                hideBottomNavigation()
                navigate(R.id.action_categoriesFragment_to_profileFragment)
            }
        }
    }

    private fun initialiseObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { subCategories ->
            addChips(
                subCategories,
                binding.chipGroup,
                layoutInflater
            ) { category ->
                viewModel.onChipClick(category as MainCategoryUiModel)
            }
        }
        viewModel.mainCategory.observe(viewLifecycleOwner, ::navigateToSubcategories)
        viewModel.userData.observe(viewLifecycleOwner, ::setupProfileImage)
    }

    private fun setupProfileImage(photoUrl: String) {
        binding.circleIvAppbar.loadUrlProfileImage(photoUrl)
    }

    private fun handleOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigate(R.id.action_categoriesFragment_to_articlesMainFragment)
                }
            })
    }

    private fun navigateToSubcategories(mainCategory: MainCategoryUiModel) {
        hideBottomNavigation()
        val action = CategoriesFragmentDirections
            .actionCategoriesFragmentToSubcategoriesFragment(mainCategory)
        navigate(action)
    }
}