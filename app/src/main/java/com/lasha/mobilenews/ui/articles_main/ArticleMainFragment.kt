package com.lasha.mobilenews.ui.articles_main

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentArticleMainBinding
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.SubCategoryUiModel
import com.lasha.mobilenews.util.LINK_PREFIX
import com.lasha.mobilenews.util.MINIMUM_LETTERS_NUMBER
import com.lasha.mobilenews.util.addChips
import com.lasha.mobilenews.util.loadUrlProfileImage
import com.lasha.mobilenews.util.navigate

class ArticleMainFragment : BaseFragment() {

    private var _binding: FragmentArticleMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by activityViewModels<ArticleMainViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentArticleMainBinding.inflate(inflater, container, false)
        startShimmerAnimation()
        initArticleRecyclerView()
        initObservers()
        showBottomNavigation()
        setupOnClickListeners()
        handleOnBackPressed()
        return binding.root
    }

    private fun handleOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().apply {
                        moveTaskToBack(true)
                        finish()
                    }
                }
            })
    }

    private fun startShimmerAnimation() {
        binding.shimmerViewContainer.startShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val list = articleRecyclerAdapter.getArticles()
        viewModel.saveScreenState(list)
        _binding = null
    }

    private fun setupOnClickListeners() {
        binding.run {
            searchEditText.doAfterTextChanged { text ->
                closeSearchButton.isVisible = text.toString().isNotEmpty()
                if (text.toString().length >= MINIMUM_LETTERS_NUMBER) {
                    viewModel.onSearchQuery(text.toString())
                }
            }
            closeSearchButton.setOnClickListener {
                searchEditText.setText("")
                val inputMethod =
                    requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethod.hideSoftInputFromWindow(requireView().windowToken, 0)
                viewModel.clearSearchData()
                closeSearchButton.isVisible = false
            }
            circleIvAppbar.setOnClickListener {
                hideBottomNavigation()
                navigate(R.id.action_articlesMainFragment_to_profileFragment)
            }

            swipeContainer.setOnRefreshListener {
                swipeContainer.isRefreshing = (false)
                viewModel.refresh(requireView())
            }
        }
    }

    private fun handleVisibilityOfProgressIndicator(isProgressVisible: Boolean) {
        when (isProgressVisible) {
            true -> binding.shimmerViewContainer.isVisible = true
            false -> binding.run {
                shimmerViewContainer.stopShimmer()
                shimmerViewContainer.isVisible = false
                articlesRecycler.isVisible = true
            }
        }
    }

    private fun initObservers() {
        viewModel.subCategories.observe(viewLifecycleOwner) { subCategories ->
            addChips(subCategories, binding.chipGroup, layoutInflater) { category ->
                viewModel.onChipClick(category as SubCategoryUiModel)
            }
        }
        viewModel.mainScreenUiState.observe(viewLifecycleOwner) {
            setupArticles(it.articleList)
            setupCheckedChips(it.pressedChips)
            setupSearchText(it.searchText)
        }
        viewModel.isDownloading.observe(viewLifecycleOwner, ::showDownloadLottie)
        viewModel.listCheckedSubCategories.observe(viewLifecycleOwner, ::setupFilteredArticles)
        viewModel.visibilityOfProgress.observe(
            viewLifecycleOwner,
            ::handleVisibilityOfProgressIndicator
        )
        viewModel.isRemoteHasError.observe(viewLifecycleOwner, ::checkRemoteError)
        viewModel.userData.observe(viewLifecycleOwner, ::setupProfileImage)
    }

    private fun setupCheckedChips(subCategoryList: List<SubCategoryUiModel>) {
        if (subCategoryList.isNotEmpty()) {
            for (element in subCategoryList) {
                binding.chipGroup.check(element.id)
            }
        }
    }

    private fun setupSearchText(searchText: String) {
        if (searchText.isNotEmpty()) {
            binding.run {
                searchEditText.setText(searchText)
            }
        }
    }

    private fun setupFilteredArticles(filters: List<SubCategoryUiModel>) {
        if (filters.isNotEmpty()) {
            viewModel.fetchFilteredArticles(filters)
        }
    }

    private fun setupArticles(articles: List<ArticleUiModel>) {
        if (articles.isNotEmpty()) {
            articleRecyclerAdapter.addArticles(articles)
            binding.nothingFoundAnimation.isVisible = false
        } else {
            binding.nothingFoundAnimation.isVisible = true
            articleRecyclerAdapter.clearArticles()
        }
    }

    private fun checkRemoteError(isDataSaveFailed: Boolean) {
        if (isDataSaveFailed) {
            showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
        }
    }

    private fun setupProfileImage(photoUrl: String) {
        binding.circleIvAppbar.loadUrlProfileImage(photoUrl)
    }

    private fun initArticleRecyclerView() {
        articleRecyclerAdapter = ArticleRecyclerAdapter(
            onBookmarkClickListener = {
                viewModel.onBookmarkClick(it)
                changeItemOnBookmarkClicked(it)
            },
            onArticleClickListener = {
                val action =
                    ArticleMainFragmentDirections.actionArticlesMainFragmentToWebViewFragment(it.id)
                navigate(action)
            },
            onShareClickListener = {
                generateSharingLink(deepLink = "${LINK_PREFIX}/webView/${it.id}".toUri()) { generatedLink ->
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, generatedLink)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            },
            onLikeClickListener = {
                viewModel.onLikeClicked(it)
                changeItemOnLikeClicked(it)
            },
            onDislikeClickListener = {
                viewModel.onDislikeClicked(it)
                changeItemOnDislikeClicked(it)
            }
        )
        binding.run {
            articlesRecycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            articlesRecycler.adapter = articleRecyclerAdapter
        }
    }

    private fun showDownloadLottie(isDownloading: Boolean) {
        with(binding) {
            if (isDownloading) {
                lottieDownload.speed = 0.5f
                lottieDownload.isVisible = true
                articlesRecycler.isVisible = false
                lottieDownload.playAnimation()
            } else {
                lottieDownload.pauseAnimation()
                lottieDownload.isVisible = false
                articlesRecycler.isVisible = true
            }
        }
    }

}