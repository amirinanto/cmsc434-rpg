package cmsc434.rpg.runner

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import cmsc434.rpg.runner.helper.*
import cmsc434.rpg.runner.list.Mission
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_tracking.*
import kotlinx.android.synthetic.main.item_mission.*
import java.lang.Float.parseFloat

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

    private var runDistance = 0f
    private var missionReq = 1f
    private var missionReward = 0
    private var missionDone = false
    private var missionNum = 0
    private var isStoryMission = false


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

        with (intent) {
            missionReq = getIntExtra(MissionActivity.MISSION_REQ, 1).toFloat()
            missionReward = getIntExtra(MissionActivity.MISSION_REWARD, 0)
            missionNum = getIntExtra(MissionActivity.MISSION_NUM, 0)
            isStoryMission = getBooleanExtra(MissionActivity.MISSION_STORY, false)
        }

        mission_info.text = "${twoDigitsPlease(missionReq)} Mile${if (missionReq == 1f) "" else "s"}"
        reward_info.text = "$missionReward Gold, $missionReward Exp"

        createLocationRequest()

        (supportFragmentManager.findFragmentById(R.id.map_view_track) as SupportMapFragment)
            .getMapAsync(this)

        finish_button.setOnClickListener {
            if (missionDone)
                finishRun()
            else
                onBackPressed()
        }

        fab_back.setOnClickListener {
            onBackPressed()
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

    override fun onBackPressed() {
        /* cheating :P
        updateDistance(1.0f)
        */

        AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?\n\n" +
                    "Mission is ${if (missionDone) "Completed" else "Not Completed"}.")
            .setPositiveButton("Yes") {
                    _,_ ->
                finishRun()
            }
            .setNegativeButton("No") {
                    _,_ ->
                Toast.makeText(this,
                    "Keep going! You got this!", Toast.LENGTH_SHORT)
                    .show()
            }.show()
    }

    private fun finishRun() {
        val miles = parseFloat(miles_info.text.toString())
        var result = Intent().putExtra(MissionActivity.MISSION_DONE, missionDone)
            .putExtra(MissionActivity.MISSION_NUM, missionNum)
            .putExtra(MissionActivity.RUN_MILES, miles)
            .putExtra(MissionActivity.RUN_REWARD, missionReward)
            .putExtra(MissionActivity.MISSION_STORY, isStoryMission)
        if (miles < 0.1f)
            setResult(Activity.RESULT_CANCELED)
        else
            setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun twoDigitsPlease(number: Float): Float {
        return String.format("%.2f", number).toFloat()
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
            lastLocation = newLocation
            trackedLocations.add(lastLocation)
            moveMap()
            return
        }

        // check if new location is good enough
        if (lastLocation.distanceTo(newLocation) > 1f) {
            addLocation(newLocation)
            /*Toast.makeText(
                applicationContext,
                "new location, lat: ${lastLocation.latitude} long: ${lastLocation.longitude}, all locations: ${trackedLocations}",
                Toast.LENGTH_SHORT
            ).show()*/

        }

        //map.addMarker(MarkerOptions().position(lastLatLng).title(lastLatLng.toString()))
    }

    private fun addLocation(location: Location){
        val lastLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        val newLatLng = LatLng(location.latitude, location.longitude)

        if (lastLatLng != newLatLng) {

            // make lines
            map.addPolyline(
                PolylineOptions()
                    .add(lastLatLng, newLatLng)
                    .width(5f)
                    .color(Color.CYAN))

            // update distance
            val distance = lastLocation.distanceTo(location) * 0.000621371192f // from meter to miles
            updateDistance(distance)

            lastLocation = location
            trackedLocations.add(lastLocation)

            // move camera to last location
            moveMap()
        }
    }

    private fun moveMap() {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, AdventureActivity.ZOOM_LEVEL))
    }

    private fun updateDistance(distance: Float) {
        runDistance += distance
        miles_info.text = twoDigitsPlease(runDistance).toString()

        val progress = (runDistance * 100 / missionReq).toInt()
        progressBar.progress = progress

        if (runDistance >= missionReq) {
            missionDone = true
            val extra = ((runDistance - missionReq) / .1).toInt()
            if (extra > 0) {
                missionReward += extra
                mission_reward.text = missionReward.toString()
            }
        }
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

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2

    }
}
