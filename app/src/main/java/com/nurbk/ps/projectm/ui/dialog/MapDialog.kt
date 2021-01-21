package com.nurbk.ps.projectm.ui.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.DialogMapBinding
import com.nurbk.ps.projectm.others.stateTheme
import com.nurbk.ps.projectm.service.LocationService


class MapDialog(val onClick: OnClickSaveLocationMap) : DialogFragment() {

    private lateinit var mBinding: DialogMapBinding
    lateinit var point: Marker
    lateinit var latLng: LatLng
    lateinit var location: LocationService
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogMapBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }

        return mBinding.root
    }

    var isAddMarker = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)


        mBinding.btnSaveLocation.setOnClickListener {
            onClick.getLocationMap(latLng)
            dismiss()
        }

        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

    }

    private lateinit var mark: Marker

    private val callback = OnMapReadyCallback { googleMap ->
        stateTheme(requireContext(), googleMap)
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()
        googleMap.setPadding(px, px, px, px)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMapClickListener {
            latLng=it
            setPadding(googleMap)
            if (!isAddMarker) {
                mark = googleMap.addMarker(
                    MarkerOptions().position(it).draggable(true)
                )
                isAddMarker = true
            } else mark.position = it
            mBinding.txtContanier.visibility = View.GONE
            mBinding.btnSaveLocation.visibility = View.VISIBLE
        }
        mBinding.floatingActionButton.isVisible = true
        mBinding.floatingActionButton.setOnClickListener {
            getLocation(googleMap)

        }

        getLocation(googleMap)
    }

    interface OnClickSaveLocationMap {
        fun getLocationMap(latLng: LatLng)
    }


    private fun getLocation(googleMap: GoogleMap) {

        location = LocationService(requireActivity(), {

            val latLng = LatLng(
                location.getLatitude(),
                location.getLongitude()
            )
            if (!isAddMarker) {
                mark = googleMap.addMarker(
                    MarkerOptions().position(latLng).draggable(true)
                )
                isAddMarker = true
            } else mark.position = latLng
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
            this.latLng = latLng
            mBinding.txtContanier.visibility = View.GONE
            mBinding.btnSaveLocation.visibility = View.VISIBLE
            setPadding(googleMap)
        }, {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            requireActivity().startActivity(intent)
        })


    }

    private fun setPadding(googleMap: GoogleMap) {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()
        val pxb = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            75f,
            resources.displayMetrics
        ).toInt()
        googleMap.setPadding(px, px, px, pxb)
    }
}