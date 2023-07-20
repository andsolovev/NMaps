package ru.netology.nmaps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmaps.R
import ru.netology.nmaps.databinding.FragmentAddBinding
import ru.netology.nmaps.db.dto.Place
import ru.netology.nmaps.viewmodel.PlaceViewModel

class AddFragment : Fragment() {

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TITLE_KEY = "NAME_KEY"
        private const val DESCR_KEY = "DESCR_KEY"
        private const val LAT_KEY = "LAT_KEY"
        private const val LONG_KEY = "LONG_KEY"
        private const val HEAD_KEY = "HEAD_KEY"

        fun createArguments(id: Long? = 0L, title: String = "", description: String = "", lat: Double, long: Double, heading: String = "") =
            bundleOf(ID_KEY to id, TITLE_KEY to title, DESCR_KEY to description, LAT_KEY to lat, LONG_KEY to long, HEAD_KEY to heading)
    }

    private val viewModel: PlaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        requireArguments().getString(TITLE_KEY)?.let(binding.nameValue::setText)
        requireArguments().getString(DESCR_KEY)?.let(binding.descriptionValue::setText)
        requireArguments().getString(HEAD_KEY)?.let{binding.heading.text = it}

        binding.addButton.setOnClickListener {
            val title = requireArguments().getString(TITLE_KEY)?.takeIf { it.isNotBlank() }
                ?: binding.nameValue.text.toString()

            title.takeIf { it.isNotBlank() } ?: run {
                Toast.makeText(requireContext(), "Name is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idArg = requireArguments().getLong(ID_KEY)
            viewModel.save(
                Place(
                    id = requireArguments().getLong(ID_KEY),
                    title = binding.nameValue.text.toString(),
                    description = if (binding.descriptionValue.text.toString() == "") "Пока нет описания" else binding.descriptionValue.text.toString(),
                    latitude = requireArguments().getDouble(LAT_KEY),
                    longitude = requireArguments().getDouble(LONG_KEY)
                )
            )

            if (idArg == 0L) {
                findNavController().navigate(
                    R.id.action_addFragment_to_mapsFragment, bundleOf(
                        MapsFragment.LAT_KEY to arguments?.getDouble(LAT_KEY),
                        MapsFragment.LONG_KEY to arguments?.getDouble(LONG_KEY)
                    )
                )
            } else findNavController().navigateUp()
        }

        binding.canselButton.setOnClickListener { findNavController().navigateUp() }

        return binding.root
    }
}