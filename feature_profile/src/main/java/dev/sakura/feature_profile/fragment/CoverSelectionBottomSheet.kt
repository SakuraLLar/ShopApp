package dev.sakura.feature_profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.sakura.feature_profile.R
import dev.sakura.feature_profile.adapter.Cover
import dev.sakura.feature_profile.adapter.CoverAdapter
import dev.sakura.feature_profile.databinding.BottomSheetCoverSelectionBinding

class CoverSelectionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCoverSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var coverAdapter: CoverAdapter
    private var selectedCover: Cover? = null

    var onSave: ((Cover) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetCoverSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialSelectedId = arguments?.getInt(ARG_CURRENT_COVER_ID) ?: -1
        val covers = getAvailableCovers()

        setupRecyclerView(covers, initialSelectedId)
        setupClickListeners()
    }

    private fun setupRecyclerView(covers: List<Cover>, initialSelectedId: Int) {
        coverAdapter = CoverAdapter(covers) { cover ->
            this.selectedCover = cover
            binding.btnSaveCover.isEnabled = true
        }

        binding.recyclerViewCovers.adapter = coverAdapter

        if (initialSelectedId != -1) {
            coverAdapter.setInitialSelection(initialSelectedId)
            selectedCover = covers.find { it.id == initialSelectedId }
        }

        binding.btnSaveCover.isEnabled = (selectedCover != null)
    }

    private fun setupClickListeners() {
        binding.btnCloseCover.setOnClickListener {
            dismiss()
        }

        binding.btnSaveCover.setOnClickListener {
            selectedCover?.let { cover ->
                onSave?.invoke(cover)
            }
            dismiss()
        }
    }

    private fun getAvailableCovers(): List<Cover> {
        return listOf(
            Cover(R.drawable.cover_gradient_lava, R.drawable.cover_gradient_lava),
            Cover(R.drawable.cover_gradient_ocean, R.drawable.cover_gradient_ocean),
            Cover(R.drawable.cover_gradient_forest, R.drawable.cover_gradient_forest),
            Cover(R.drawable.cover_gradient_love, R.drawable.cover_gradient_love),
            Cover(R.drawable.cover_gradient_space, R.drawable.cover_gradient_space),
            Cover(R.drawable.cover_gradient_sunset, R.drawable.cover_gradient_sunset),
            Cover(R.drawable.cover_gradient_forest, R.drawable.cover_gradient_forest),
            Cover(R.drawable.cover_gradient_lavender, R.drawable.cover_gradient_lavender),
            Cover(R.drawable.cover_gradient_cloud, R.drawable.cover_gradient_cloud),
            Cover(R.drawable.cover_gradient_wine, R.drawable.cover_gradient_wine),
            Cover(R.drawable.cover_gradient_bitumen, R.drawable.cover_gradient_bitumen),
            Cover(R.drawable.cover_gradient_flowers, R.drawable.cover_gradient_flowers),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CoverSelectionBottomSheet"
        private const val ARG_CURRENT_COVER_ID = "current_cover_id"

        fun newInstance(currentCoverId: Int): CoverSelectionBottomSheet {
            val args = Bundle().apply {
                putInt(ARG_CURRENT_COVER_ID, currentCoverId)
            }
            return CoverSelectionBottomSheet().apply {
                arguments = args
            }
        }
    }
}
