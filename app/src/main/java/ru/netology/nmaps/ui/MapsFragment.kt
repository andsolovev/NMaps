package ru.netology.nmaps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.cameraPosition
import com.google.maps.android.ktx.utils.collection.addMarker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmaps.R
import ru.netology.nmaps.extensions.icon
import ru.netology.nmaps.viewmodel.PlaceViewModel


class MapsFragment : Fragment(), GoogleMap.OnPoiClickListener, GoogleMap.OnMapClickListener {

    companion object {
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"
    }

    private lateinit var googleMap: GoogleMap

    private val viewModel by viewModels<PlaceViewModel>()

    private val menuHost: MenuHost get() = requireActivity()

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                // TODO: show sorry dialog
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)

    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                findNavController().navigate(R.id.action_mapsFragment_to_placesFragment)
                return true
            }
        }, viewLifecycleOwner)

        lifecycleScope.launchWhenCreated {
            googleMap = mapFragment.awaitMap()

            googleMap.apply {

                isTrafficEnabled = false
                isBuildingsEnabled = true
                uiSettings.apply {
                    isZoomControlsEnabled = true
                    setAllGesturesEnabled(true)
                }
            }

            googleMap.setOnPoiClickListener(this@MapsFragment)
            googleMap.setOnMapClickListener(this@MapsFragment)
            googleMap.setOnMapLongClickListener { }

            when {
                // 1. Проверяем есть ли уже права
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    googleMap.apply {
                        isMyLocationEnabled = true
                        uiSettings.isMyLocationButtonEnabled = true
                    }

                    val fusedLocationProviderClient = LocationServices
                        .getFusedLocationProviderClient(requireActivity())

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        println(it)
                    }
                }
                // 2. Должны показать обоснование необходимости прав
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    // TODO: show rationale dialog
                }
                // 3. Запрашиваем права
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            val arguments = arguments
            if (arguments != null &&
                arguments.containsKey(LAT_KEY) &&
                arguments.containsKey(LONG_KEY)
            ) {
                val target = LatLng(
                    arguments.getDouble(LAT_KEY), arguments.getDouble(
                        LONG_KEY
                    )
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15f))
                arguments.remove(LAT_KEY)
                arguments.remove(LONG_KEY)
            } else {
                val target = LatLng(55.751999, 37.617734)
                googleMap.awaitAnimateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        cameraPosition {
                            target(target)
                            zoom(15F)
                        }
                    ))
            }

            val markerManager = MarkerManager(googleMap)
            val collection: MarkerManager.Collection = markerManager.newCollection()

//            collection.setOnMarkerClickListener { marker ->
//                // TODO: work with marker
//                Toast.makeText(
//                    requireContext(),
//                    """ ${marker.title}
//                        ${marker.tag.toString()}
//                    """.trimIndent(),
//                    Toast.LENGTH_LONG
//                ).show()
//                true
//            }

            viewModel.data.collectLatest { places ->
                if (places.isEmpty() && getView()?.isShown == true) {
                    delay(2_500)
                    FirstStartDialog().show(childFragmentManager, null)
                }
                collection.clear()
                places.forEach { place ->
                    collection.apply {
                        addMarker {
                            position(LatLng(place.latitude, place.longitude))
                            title(place.title)
                            icon(getDrawable(requireContext(), R.drawable.baseline_point_24)!!)
                            snippet(place.description)
                        }.apply {
                            tag = place.description
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onPoiClick(poi: PointOfInterest) {
        Snackbar.make(requireView(), poi.name, 7000).apply {
            setAction("Add") {
                findNavController().navigate(
                    R.id.action_mapsFragment_to_addFragment,
                    AddFragment.createArguments(
                        title = poi.name,
                        lat = poi.latLng.latitude,
                        long = poi.latLng.longitude,
                        heading = "Добавить в Мой список:"
                    )
                )
            }
            setTextMaxLines(2)
            setTextColor(R.color.black)
            show()
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onMapClick(point: LatLng) {
        Snackbar.make(
            requireView(),
            """Точка с координатами:
                | ${point.latitude}, ${point.longitude}
            """.trimMargin(),
            5000
        ).apply {
            setAction("Add") {
                findNavController().navigate(
                    R.id.action_mapsFragment_to_addFragment,
                    AddFragment.createArguments(lat = point.latitude, long = point.longitude)
                )
            }
            setTextMaxLines(2)
            setTextColor(R.color.black)
            show()
        }
    }
}