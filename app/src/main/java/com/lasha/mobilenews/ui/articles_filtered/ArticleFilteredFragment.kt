package com.lasha.mobilenews.ui.articles_filtered

import android.content.Context
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.lasha.mobilenews.databinding.FragmentArticleFilteredBinding
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.articles_main.ArticleRecyclerAdapter
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.util.LINK_PREFIX
import com.lasha.mobilenews.util.MINIMUM_LETTERS_NUMBER
import com.lasha.mobilenews.util.navigate

class ArticleFilteredFragment : BaseFragment() {

    private var _binding: FragmentArticleFilteredBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val navArgs by navArgs<ArticleFilteredFragmentArgs>()

    private val viewModel by viewModels<ArticleFilteredViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentArticleFilteredBinding.inflate(inflater, container, false)
        viewModel.fetchFilteredArticles(navArgs.itemClicked, navArgs.category)
        startShimmerAnimation()
        initArticleRecyclerView()
        initObservers()
        setupOnClickListeners()
        handleOnBackPressed()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        articleRecyclerAdapter.clearArticles()
    }

    private fun handleOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action =
                        ArticleFilteredFragmentDirections.actionArticleFilteredFragmentToSubcategoriesFragment(
                            navArgs.category
                        )
                    navigate(action)
                }
            })
    }

    private fun startShimmerAnimation() {
        binding.shimmerViewContainer.startShimmer()
    }

    private fun checkDataIsSaved(isDataSaveFailed: Boolean) {
        if (isDataSaveFailed) {
            showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
        }
    }

    private fun setupOnClickListeners() {
        binding.run {
            searchEt.doAfterTextChanged { text ->
                closeSearchBtn.isVisible = text.toString().isNotEmpty()
                if (text.toString().length >= MINIMUM_LETTERS_NUMBER) {
                    viewModel.onSearchQuery(text.toString())
                } else {
                    viewModel.setDefaultArticles()
                }
            }
            closeSearchBtn.setOnClickListener {
                searchEt.setText("")
                val inputMethod =
                    requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethod.hideSoftInputFromWindow(requireView().windowToken, 0)
                viewModel.setDefaultArticles()
                closeSearchBtn.isVisible = false
            }
            appbar.setNavigationOnClickListener {
                val action =
                    ArticleFilteredFragmentDirections.actionArticleFilteredFragmentToSubcategoriesFragment(
                        navArgs.category
                    )
                navigate(action)
            }
        }
    }

    private fun initObservers() {
        viewModel.visibilityOfProgress.observe(
            viewLifecycleOwner,
            ::handleVisibilityOfProgressIndicator
        )
        viewModel.articlesData.observe(viewLifecycleOwner, ::setupArticles)
        viewModel.isRemoteHasError.observe(viewLifecycleOwner, ::checkDataIsSaved)
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

    private fun setupArticles(articles: List<ArticleUiModel>) {
        if (articles.isNotEmpty()) {
            articleRecyclerAdapter.addArticles(articles)
            binding.nothingFoundAnimation.isVisible = false
        } else {
            articleRecyclerAdapter.clearArticles()
            binding.nothingFoundAnimation.isVisible = true
        }
    }

    private fun initArticleRecyclerView() {
        articleRecyclerAdapter = ArticleRecyclerAdapter(
            onBookmarkClickListener = {
                viewModel.onBookmarkClick(it)
                changeItemOnBookmarkClicked(it)
            },
            onArticleClickListener = {
                val action =
                    ArticleFilteredFragmentDirections.actionArticleFilteredFragmentToWebViewFragment(
                        it.id
                    )
                navigate(action)
            },
            onShareClickListener = {
                generateSharingLink(deepLink = "$LINK_PREFIX/webView/${it.id}".toUri()) { generatedLink ->
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
}