package com.lasha.mobilenews.ui.articles_main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.ItemArticleBinding
import com.lasha.mobilenews.ui.models.ArticleUiModel
import com.lasha.mobilenews.ui.models.OnBookmarkClick
import com.lasha.mobilenews.ui.models.OnLikeClick
import com.lasha.mobilenews.util.loadUrlImage

const val LIKED_CHANGED = "Liked Changed"
const val DISLIKED_CHANGED = "DisLiked Changed"
const val BOOKMARK_CHANGED = "Bookmark Changed"

class ArticleRecyclerAdapter(
    private val onBookmarkClickListener: (OnBookmarkClick) -> Unit,
    private val onShareClickListener: (ArticleUiModel) -> Unit,
    private val onArticleClickListener: (ArticleUiModel) -> Unit,
    private val onLikeClickListener: (OnLikeClick) -> Unit,
    private val onDislikeClickListener: (OnLikeClick) -> Unit,
) : RecyclerView.Adapter<ArticleViewHolder>() {

    private var articleList = ArrayList<ArticleUiModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun addArticles(newArticles: List<ArticleUiModel>) {
        articleList.clear()
        articleList.addAll(newArticles)
        notifyDataSetChanged()
    }

    fun getArticles(): List<ArticleUiModel> {
        return articleList
    }

    fun changeArticle(newArticle: ArticleUiModel, oldArticle: ArticleUiModel, payload: String) {
        val position = articleList.indexOf(oldArticle)
        if (position != NO_POSITION) {
            articleList[position] = newArticle
            notifyItemChanged(position, payload)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearArticles() {
        articleList.clear()
        notifyDataSetChanged()
    }

    fun removeArticle(item: ArticleUiModel) {
        val position = articleList.indexOf(item)
        if (position != NO_POSITION) {
            articleList.remove(item)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onBookmarkClickListener,
            onShareClickListener,
            onArticleClickListener,
            onLikeClickListener,
            onDislikeClickListener
        )
    }

    override fun onBindViewHolder(
        holder: ArticleViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = articleList[position]

        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                LIKED_CHANGED -> holder.bindLikedIcon(item.liked)
                DISLIKED_CHANGED -> holder.bindDislikedIcon(item.disliked)
                BOOKMARK_CHANGED -> holder.bindBookmarkedIcon(item.bookmarked)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val item = articleList[position]
        holder.bindItem(item)
    }

    override fun getItemCount() = articleList.size

    companion object {
        const val NO_POSITION = -1
    }
}

class ArticleViewHolder(
    private val binding: ItemArticleBinding,
    private val onBookmarkClickListener: (OnBookmarkClick) -> Unit,
    private val onShareClickListener: (ArticleUiModel) -> Unit,
    private val onArticleClickListener: (ArticleUiModel) -> Unit,
    private val onLikeClickListener: (OnLikeClick) -> Unit,
    private val onDislikeClickListener: (OnLikeClick) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {

    private var isBookmarkClicked = false
    private var isLikeClicked = false
    private var isDislikeClicked = false

    fun bindItem(item: ArticleUiModel) {
        binding.run {
            setupImageViews(item)
            setupTextViews(item)
            setupClickListeners(item)
        }
    }

    fun bindLikedIcon(liked: Boolean) {
        isLikeClicked = liked
    }

    fun bindDislikedIcon(disliked: Boolean) {
        isDislikeClicked = disliked
    }

    fun bindBookmarkedIcon(bookmarked: Boolean) {
        isBookmarkClicked = bookmarked
    }

    private fun ItemArticleBinding.setupTextViews(item: ArticleUiModel) {
        articleTitleTv.text = item.title
        articleCategoryTv.text = item.category
        articleSubCategoryTv.text = item.subcategory[0]
        likesTextView.text = item.likes.toString()
        dislikesTextView.text = item.dislikes.toString()
    }

    private fun ItemArticleBinding.setupImageViews(
        item: ArticleUiModel
    ) {
        articleIv.loadUrlImage(item.pictureUrl)
        isBookmarkClicked = item.bookmarked
        isDislikeClicked = item.disliked
        isLikeClicked = item.liked
        when {
            item.bookmarked -> {
                bookmarkBtn.setImageResource(R.drawable.ic_bookmark_checked)
            }

            else -> {
                bookmarkBtn.setImageResource(R.drawable.ic_bookmark_unchecked)
            }
        }
        when {
            item.liked -> {
                likeBtn.setImageResource(R.drawable.baseline_thumb_up_24)
            }

            else -> {
                likeBtn.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
            }
        }
        when {
            item.disliked -> {
                dislikeBtn.setImageResource(R.drawable.baseline_thumb_down_24)
            }

            else -> {
                dislikeBtn.setImageResource(R.drawable.baseline_thumb_down_off_alt_24)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun ItemArticleBinding.setupClickListeners(item: ArticleUiModel) {
        var currentLikes = item.likes
        var currentDislikes = item.dislikes
        shareBtn.setOnClickListener {
            onShareClickListener.invoke(item)
        }
        bookmarkBtn.setOnClickListener {
            if (isBookmarkClicked) {
                isBookmarkClicked = false
                bookmarkBtn.setImageResource(R.drawable.ic_bookmark_unchecked)
            } else {
                isBookmarkClicked = true
                bookmarkBtn.setImageResource(R.drawable.ic_bookmark_checked)
            }
            onBookmarkClickListener.invoke(OnBookmarkClick(item, isBookmarkClicked))
        }
        articleItem.setOnClickListener {
            onArticleClickListener.invoke(item)
        }
        likeBtn.setOnClickListener {
            val wasDislikeClicked = isDislikeClicked
            currentLikes = if (isLikeClicked) {
                unCheckLike(currentLikes)
            } else {
                if (isDislikeClicked) {
                    unCheckDislike(currentDislikes)
                }
                checkLike(currentLikes)
            }
            onLikeClickListener.invoke(OnLikeClick(item, isLikeClicked, wasDislikeClicked))
        }
        dislikeBtn.setOnClickListener {
            val wasLikeClicked = isLikeClicked
            if (isDislikeClicked) {
                currentDislikes = unCheckDislike(currentDislikes)
            } else {
                if (isLikeClicked) {
                    currentLikes = unCheckLike(currentLikes)
                }
                currentDislikes = checkDislike(currentDislikes)
            }
            onDislikeClickListener.invoke(OnLikeClick(item, wasLikeClicked, isDislikeClicked))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun ItemArticleBinding.checkLike(currentLikes: Int): Int {
        isLikeClicked = true
        likeBtn.setImageResource(R.drawable.baseline_thumb_up_24)
        likesTextView.text = (currentLikes + UPDATE_LIKES).toString()
        return currentLikes + UPDATE_LIKES
    }

    private fun ItemArticleBinding.unCheckDislike(currentDislikes: Int): Int {
        isDislikeClicked = false
        dislikeBtn.setImageResource(R.drawable.baseline_thumb_down_off_alt_24)
        dislikesTextView.text = (currentDislikes - UPDATE_LIKES).toString()
        return currentDislikes - UPDATE_LIKES
    }

    @SuppressLint("SetTextI18n")
    private fun ItemArticleBinding.checkDislike(currentDislikes: Int): Int {
        isDislikeClicked = true
        dislikeBtn.setImageResource(R.drawable.baseline_thumb_down_24)
        dislikesTextView.text = (currentDislikes + UPDATE_LIKES).toString()
        return currentDislikes + UPDATE_LIKES
    }

    private fun ItemArticleBinding.unCheckLike(currentLikes: Int): Int {
        isLikeClicked = false
        likeBtn.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
        likesTextView.text = (currentLikes - UPDATE_LIKES).toString()
        return currentLikes - UPDATE_LIKES
    }

    companion object {
        const val UPDATE_LIKES = 1
    }
}