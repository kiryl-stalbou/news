package com.lasha.mobilenews.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentErrorBottomSheetBinding
import com.lasha.mobilenews.util.getSerializable

class ErrorBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentErrorBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErrorBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val error = arguments?.getSerializable<Error>(ERROR_KEY)
        error?.let { chooseCurrentState(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun chooseCurrentState(state: Error) {
        binding?.run {
            when (state) {
                Error.NO_INTERNET_CONNECTION -> {
                    descriptionOfErrorTextView.setText(R.string.you_have_no_internet_connection)
                }
                Error.SERVER_IS_NOT_RESPONDING -> {
                    descriptionOfErrorTextView.setText(R.string.you_have_error_with_server)
                }
                Error.TIME_OUT -> {
                    descriptionOfErrorTextView.setText(R.string.your_waiting_time_is_up)
                }
                Error.ERROR_WITH_AUTHENTICATION -> {
                    descriptionOfErrorTextView.setText(R.string.can_not_upload_data_to_firebase)
                }
                Error.ERROR_WITH_REALTIME_DB -> {
                    descriptionOfErrorTextView.setText(R.string.can_not_upload_data_to_firebase)
                }
                Error.NOTHING_FOUND -> {
                    descriptionOfErrorTextView.setText(R.string.nothing_found)
                }
            }
            cancelTextView.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {

        private const val ERROR_KEY = "errorKey"

        fun newInstance(error: Error) = ErrorBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ERROR_KEY, error)
            }
        }
    }
}