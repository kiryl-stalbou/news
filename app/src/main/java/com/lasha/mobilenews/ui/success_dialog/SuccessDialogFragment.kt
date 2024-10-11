package com.lasha.mobilenews.ui.success_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lasha.mobilenews.R
import com.lasha.mobilenews.databinding.FragmentDialogSucccessFinishBinding
import com.lasha.mobilenews.util.navigate

class SuccessDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogSucccessFinishBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogSucccessFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.SuccessFinishDialog)
        dialog.window?.attributes?.windowAnimations = R.style.SuccessFinishDialogAnimation
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.letsGetStartedButton.setOnClickListener {
            navigate(R.id.action_successDialogFragment_to_articlesMainFragment)
        }
    }
}