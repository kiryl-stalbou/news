package com.lasha.mobilenews.ui.web_view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentWebViewBinding
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.common.BaseFragment
import com.lasha.mobilenews.ui.common.Error
import com.lasha.mobilenews.ui.models.OnBookmarkClick
import com.lasha.mobilenews.util.LINK_PREFIX

class WebViewFragment : BaseFragment() {

    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val navArgs by navArgs<WebViewFragmentArgs>()

    private val viewModel by viewModels<WebViewViewModel>() { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).component.inject(this)
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        viewModel.getArticle(navArgs.id)
        setupShareClickListener()
        initializeObservers()
        hideBottomNavigation()
        setupBackClicked()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(article: ArticleUiModel) {
        binding.run {
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webView.apply {
                webViewClient = WebViewClient()
                loadUrl(article.articleUrl)
                canGoBack()
                setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP && canGoBack()) {
                        goBack()
                        return@OnKeyListener true
                    }
                    false
                })
            }
        }
    }

    private fun setupShareClickListener() {
        binding.shareBtn.setOnClickListener {
            generateSharingLink(deepLink = "$LINK_PREFIX/webView/${navArgs.id}".toUri()) { generatedLink ->
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, generatedLink)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
    }

    private fun setupBookmarkActions(article: ArticleUiModel) {
        var isBookmarked: Boolean
        binding.run {
            isBookmarked = when (article.bookmarked) {
                true -> {
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmark_checked_white)
                    article.bookmarked
                }
                false -> {
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmark_unchecked_white)
                    article.bookmarked
                }
            }
            bookmarkBtn.setOnClickListener {
                when (isBookmarked) {
                    true -> {
                        isBookmarked = false
                        bookmarkBtn.setImageResource(R.drawable.ic_bookmark_unchecked_white)
                        viewModel.onBookmarkClick(OnBookmarkClick(article, isBookmarked))
                    }
                    false -> {
                        isBookmarked = true
                        bookmarkBtn.setImageResource(R.drawable.ic_bookmark_checked_white)
                        viewModel.onBookmarkClick(OnBookmarkClick(article, isBookmarked))
                    }
                }
            }
        }
    }

    private fun setupBackClicked() {
        binding.appbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initializeObservers() {
        viewModel.selectedArticle.observe(viewLifecycleOwner) {
            setupWebView(it)
            setupBookmarkActions(it)
        }
        viewModel.isRemoteHasError.observe(viewLifecycleOwner, ::checkRemoteError)
    }

    private fun checkRemoteError(isDataSaveFailed: Boolean) {
        if (isDataSaveFailed) {
            showErrorBottomSheet(Error.ERROR_WITH_REALTIME_DB)
        }
    }
}