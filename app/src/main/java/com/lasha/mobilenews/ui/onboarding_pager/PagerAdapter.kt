package com.lasha.mobilenews.ui.onboarding_pager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lasha.mobilenews.databinding.ItemOnboardingBinding
import com.lasha.mobilenews.ui.models.OnBoardingPageData

class PagerAdapter(private val onBoardingList: List<OnBoardingPageData>) :
    RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(onBoarding: OnBoardingPageData) {
            binding.pagerTv.text = onBoarding.title
            binding.animationView.setAnimation(onBoarding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindItem(onBoardingList[position])
    }

    override fun getItemCount(): Int {
        return onBoardingList.size
    }
}