package com.nurbk.ps.projectm.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.nurbk.ps.projectm.others.permission

class LocationService(
    private val mContext: Context,
    val onGranted: () -> Unit, val onDenied: () -> Unit
) :
    LocationListener {

    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    private var canGetLocation = false
    private var loca: Location? = null
    private var lat = 0.0
    private var longe = 0.0

    // Declaring a Location Manager
    private var locationManager: LocationManager? = null
    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {

        try {
            locationManager =
                mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert()
            } else {
                canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    //check the network permission
                    permission(
                        context = mContext,
                        arrayListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), {
                            onGranted()
                            locationManager!!.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                            )
                            if (locationManager != null) {
                                loca = locationManager!!
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                if (loca != null) {
                                    lat = loca!!.latitude
                                    longe = loca!!.longitude
                                }
                            }
                        }, {
                            onDenied()
                        }
                    )

                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (loca == null) {
                        //check the network permission
                        permission(
                            context = mContext,
                            arrayListOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), {
                                onGranted()
                                locationManager!!.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                                )
                                if (locationManager != null) {
                                    loca = locationManager!!
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                    if (loca != null) {
                                        lat = loca!!.latitude
                                        longe = loca!!.longitude
                                    }
                                }
                            }, {
                                onDenied()
                            }
                        )

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return loca
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (loca != null) {
            lat = loca!!.latitude
        }

        // return latitude
        return lat
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (loca != null) {
            longe = loca!!.longitude
        }

        // return longitude
        return longe
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
            DialogInterface.OnClickListener { dialog, which ->
                val intent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            })

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {
        Log.e("onLocationChanged", location.latitude.toString())
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(
        provider: String,
        status: Int,
        extras: Bundle
    ) {
    }


    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 60 * 1 // 1 minute
            .toLong()

    }

    init {
        getLocation()
    }
}