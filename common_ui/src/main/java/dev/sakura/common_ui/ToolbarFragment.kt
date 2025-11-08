package dev.sakura.common_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dev.sakura.common_ui.databinding.FragmentToolbarBinding

class ToolbarFragment : Fragment() {
    private var _binding: FragmentToolbarBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var showBackButton: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            showBackButton = it.getBoolean(ARG_SHOW_BACK_BUTTON, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentToolbarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarFragment)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            this.title = this@ToolbarFragment.title
            setDisplayHomeAsUpEnabled(showBackButton)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_SHOW_BACK_BUTTON = "arg_show_back_button"

        @JvmStatic
        fun newInstance(title: String? = null, showBackButton: Boolean = false) =
            ToolbarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putBoolean(ARG_SHOW_BACK_BUTTON, showBackButton)
                }
            }
    }
}
