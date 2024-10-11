package com.lasha.mobilenews.ui.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.lasha.mobilenews.ui.MainActivity
import com.lasha.mobilenews.ui.articles_main.ArticleRecyclerAdapter
import com.lasha.mobilenews.ui.articles_main.BOOKMARK_CHANGED
import com.lasha.mobilenews.ui.articles_main.DISLIKED_CHANGED
import com.lasha.mobilenews.ui.articles_main.LIKED_CHANGED
import com.lasha.mobilenews.ui.models.OnBookmarkClick
import com.lasha.mobilenews.ui.models.OnLikeClick
import com.lasha.mobilenews.util.ConnectivityLiveData
import com.lasha.mobilenews.util.LINK_PREFIX
import javax.inject.Inject

open class BaseFragment : Fragment() {

    private var errorBottomSheet: ErrorBottomSheetFragment? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var connectivityLiveData: ConnectivityLiveData

    lateinit var articleRecyclerAdapter: ArticleRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).component.inject(this)
        handleNoInternetConnection()
        initialiseConnectivityObserver()
    }

    fun showErrorBottomSheet(error: Error) {
        errorBottomSheet = ErrorBottomSheetFragment.newInstance(error)
        errorBottomSheet?.show(parentFragmentManager, null)
    }

    fun closeErrorBottomSheet() {
        if (isBottomSheetVisible() == true) {
            errorBottomSheet?.dismiss()
        }
    }

    fun showBottomNavigation() {
        (requireActivity() as MainActivity).getNav().isVisible = true
    }

    fun hideBottomNavigation() {
        (requireActivity() as MainActivity).getNav().isVisible = false
    }

    private fun isBottomSheetVisible() = errorBottomSheet?.isVisible

    private fun handleNoInternetConnection() {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetwork == null) {
            showErrorBottomSheet(Error.NO_INTERNET_CONNECTION)
        }
    }

    open fun handleInternetConnection(isAvailable: Boolean) {
        if (isAvailable) {
            closeErrorBottomSheet()
        } else {
            showErrorBottomSheet(Error.NO_INTERNET_CONNECTION)
        }
    }


    fun generateSharingLink(
        deepLink: Uri,
        getShareableLink: (String) -> Unit = {},
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            link = deepLink
            domainUriPrefix = LINK_PREFIX
            androidParameters {
                build()
            }
            buildShortDynamicLink()
        }.also {
            it.addOnSuccessListener { dynamicLink ->
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
        }
    }

    fun changeItemOnBookmarkClicked(it: OnBookmarkClick) {
        articleRecyclerAdapter.changeArticle(
            it.model.copy(bookmarked = !it.model.bookmarked),
            it.model, BOOKMARK_CHANGED
        )
    }

    fun changeItemOnLikeClicked(it: OnLikeClick) {
        when {
            it.isLikeClicked && it.isDislikeClicked -> {
                articleRecyclerAdapter.changeArticle(
                    it.model.copy(
                        liked = true,
                        likes = it.model.likes + 1,
                        disliked = false,
                        dislikes = it.model.dislikes - 1
                    ), it.model, LIKED_CHANGED
                )
            }

            it.isLikeClicked && !it.isDislikeClicked -> {
                articleRecyclerAdapter.changeArticle(
                    it.model.copy(
                        liked = true,
                        likes = it.model.likes + 1
                    ), it.model, LIKED_CHANGED
                )
            }

            !it.isLikeClicked && !it.isDislikeClicked -> {
                articleRecyclerAdapter.changeArticle(
                    it.model.copy(
                        liked = false,
                        likes = it.model.likes - 1
                    ), it.model, LIKED_CHANGED
                )
            }
        }
    }

    fun changeItemOnDislikeClicked(it: OnLikeClick) {
        when {
            it.isLikeClicked && it.isDislikeClicked -> {
                articleRecyclerAdapter.changeArticle(
                    it.model.copy(
                        disliked = true,
                        dislikes = it.model.dislikes + 1,
                        liked = false,
                        likes = it.model.likes - 1
                    ), it.model, DISLIKED_CHANGED
                )
            }

            !it.isLikeClicked && it.isDislikeClicked -> {
                articleRecyclerAdapter.changeArticle(
                    it.model.copy(
                        disliked = true,
                        dislikes = it.model.dislikes + 1
                    ), it.model, DISLIKED_CHANGED
                )
            }

            !it.isLikeClicked && !it.isDislikeClicked -> {
                articleRecyclerAdapter.changeArticle(
                    it.model.copy(
                        disliked = false,
                        dislikes = it.model.dislikes - 1
                    ), it.model, DISLIKED_CHANGED
                )
            }
        }
    }

    private fun initialiseConnectivityObserver() {
        connectivityLiveData.observe(viewLifecycleOwner, ::handleInternetConnection)
    }
}