package com.lasha.mobilenews.ui.saved_articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lasha.mobilenews.databinding.FragmentSavedArticlesBinding
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.articles_main.ArticleRecyclerAdapter
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.util.LINK_PREFIX
import com.lasha.mobilenews.util.navigate

class SavedArticlesFragment : BaseFragment() {

    private var _binding: FragmentSavedArticlesBinding? = null
    private val binding get() = requireNotNull(_binding)


    private val viewModel by viewModels<SavedArticlesViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startShimmerAnimation()
        setupBackClickListener()
        initArticleRecyclerView()
        initializeObservers()
    }

    private fun startShimmerAnimation() {
        binding.shimmerViewContainer.startShimmer()
    }

    private fun initializeObservers() {
        viewModel.visibilityOfProgress.observe(
            viewLifecycleOwner,
            ::handleVisibilityOfProgressIndicator
        )
        viewModel.articlesData.observe(viewLifecycleOwner, ::setupArticles)
        viewModel.isRemoteHasError.observe(viewLifecycleOwner, ::checkDataIsSaved)
        viewModel.isDataFetchFailed.observe(viewLifecycleOwner) {
            handleDataFetchFailed()
        }
        viewModel.removedArticle.observe(viewLifecycleOwner, ::removeArticleFromRecycler)
    }

    private fun removeArticleFromRecycler(article: ArticleUiModel) {
        articleRecyclerAdapter.removeArticle(article)
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
            binding.nothingFoundAnimation.isVisible = true
        }
    }

    private fun checkDataIsSaved(isDataSaveFailed: Boolean) {
        if (isDataSaveFailed) {
            showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
        }
    }

    private fun handleDataFetchFailed() {
        showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
    }

    private fun initArticleRecyclerView() {
        articleRecyclerAdapter = ArticleRecyclerAdapter(
            onBookmarkClickListener = {
                viewModel.onBookmarkClick(it)
                changeItemOnBookmarkClicked(it)
            },
            onArticleClickListener = {
                val action =
                    SavedArticlesFragmentDirections.actionSavedArticlesFragmentToWebViewFragment(it.id)
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

    private fun setupBackClickListener() {
        binding.appbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}