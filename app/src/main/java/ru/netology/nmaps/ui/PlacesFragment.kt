package ru.netology.nmaps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmaps.R
import ru.netology.nmaps.adapter.PlaceAdapter
import ru.netology.nmaps.databinding.FragmentPlacesBinding
import ru.netology.nmaps.dto.Place
import ru.netology.nmaps.viewmodel.PlaceViewModel

class PlacesFragment : Fragment() {

    private val menuHost: MenuHost get() = requireActivity()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlacesBinding.inflate(inflater, container, false)

        val viewModel by viewModels<PlaceViewModel>()


        val adapter = PlaceAdapter(object : PlaceAdapter.Listener {

            override fun onClick(place: Place) {
                findNavController().navigate(
                    R.id.action_placesFragment_to_mapsFragment, bundleOf(
                        MapsFragment.LAT_KEY to place.latitude,
                        MapsFragment.LONG_KEY to place.longitude
                    )
                )
            }

            override fun onDelete(place: Place) {
                viewModel.remove(place.id)
            }

            override fun onEdit(place: Place) {
                findNavController().navigate(R.id.action_placesFragment_to_addFragment,
                AddFragment.createArguments(lat = place.latitude, long = place.longitude, id = place.id, title = place.title, description = place.description, heading = "Редактировать Место:"))
            }
        })

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.places_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                findNavController().navigateUp()
                return true
            }
        }, viewLifecycleOwner)

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.data.collectLatest { places ->
                adapter.submitList(places)
                binding.emptyList.isVisible = places.isEmpty()
                binding.clearButton.isVisible = places.isNotEmpty()
            }
        }

        binding.clearButton.setOnClickListener {
            viewModel.removeAll()
        }

        return binding.root
    }
}