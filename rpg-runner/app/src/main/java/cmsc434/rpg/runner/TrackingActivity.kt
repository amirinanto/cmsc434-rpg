package cmsc434.rpg.runner

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import cmsc434.rpg.runner.helper.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_tracking.*

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var pref: PrefHelper
    private lateinit var player: PlayerHelper

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var locationRequest : LocationRequest
    private lateinit var lastLocation : Location
    private var locationUpdateState = false
    private var trackedLocations = ArrayList<Location>()


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        pref = applicationContext.pref
        player = applicationContext.player

        fusedLocationClient = FusedLocationProviderClient(this)
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                val newLocation = result.lastLocation
                updateLocation(newLocation)
            }
        }

        createLocationRequest()

        (supportFragmentManager.findFragmentById(R.id.map_view_track) as SupportMapFragment)
            .getMapAsync(this)

        finish_button.setOnClickListener {
            if (lastLocation != null)
                Toast.makeText(applicationContext,
                    "distance: ${lastLocation.distanceTo(trackedLocations[0])}, accuracy: ${lastLocation.accuracy}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!locationUpdateState)
            startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /* Map Related Functions */

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        setupMap()
        setupLocation()
    }

    private fun setupMap() {
        if (!isLocationAllowed()) {
            requestPermission()
            return
        }

        map.setCustomMapStyle(applicationContext.getMapStyle(pref))
    }

    /* Location Related Functions */

    private fun requestPermission() =
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

    private fun isLocationAllowed(): Boolean =
        ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true

                setupMap()
                startLocationUpdates()
            }
        }
    }

    private fun setupLocation() {
        createLocationRequest()
        startLocationUpdates()
    }

    private fun updateLocation(newLocation: Location?) {
        if (newLocation == null)
            return

        // first location
        if (!::lastLocation.isInitialized) {
            addLocation(newLocation)
            return
        }

        // check if new location is good enough
        if (lastLocation.distanceTo(newLocation) >= 10f) {
            addLocation(newLocation)
            Toast.makeText(
                applicationContext,
                "new location, lat: ${lastLocation.latitude} long: ${lastLocation.longitude}, all locations: ${trackedLocations}",
                Toast.LENGTH_SHORT
            ).show()

        }

        //map.addMarker(MarkerOptions().position(lastLatLng).title(lastLatLng.toString()))
    }

    private fun addLocation(location: Location){
        lastLocation = location
        trackedLocations.add(lastLocation)

        // move camera to last location
        val lastLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, AdventureActivity.ZOOM_LEVEL))
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        LocationServices.getSettingsClient(this)
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .build())
            .addOnSuccessListener {
                locationUpdateState = true
                startLocationUpdates()
            }.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        e.startResolutionForResult(this,
                            REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

    private fun startLocationUpdates() {
        if (!isLocationAllowed()) {
            requestPermission()
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }


    companion object {
        const val TAG = "RPG_Tracking"

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2

    }
}
