package cmsc434.rpg.runner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import cmsc434.rpg.runner.entity.MapItemView
import cmsc434.rpg.runner.helper.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import kotlinx.android.synthetic.main.activity_adventure.*
import kotlinx.android.synthetic.main.activity_adventure.fab_back
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class AdventureActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap

    private var lastId: Int = 0

    private lateinit var pref: PrefHelper
    private lateinit var player: PlayerHelper

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var locationRequest : LocationRequest
    private lateinit var lastLocation : Location
    private var locationUpdateState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure)

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

        fab_menu.setOnClickListener {
            if (menu_items.isVisible)
                menu_items.visibility = View.GONE
            else
                menu_items.visibility = View.VISIBLE
        }

        run_submenu_text.setOnClickListener{ openRunMenu() }
        run_submenu.setOnClickListener { openRunMenu() }

        option_submenu_text.setOnClickListener{ openOptionMenu() }
        option_submenu.setOnClickListener { openOptionMenu() }

        fab_back.setOnClickListener {
            finish()
        }

        (supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment)
            .getMapAsync(this)

        if (player.isExist())
            updatePlayer()
        else
            player.initPlayer("Ryu")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestLocationPermission()
        }

    }

    private fun openOptionMenu() {
        animateTextButton(option_submenu_text)
        startActivity(Intent(this, OptionActivity::class.java))
    }

    private fun openRunMenu(){
        animateTextButton(run_submenu_text)
        startActivity(Intent(this, MissionActivity::class.java))
    }

    private fun animateTextButton(text: TextView) {
        text.animate()
            .alpha(0.6f)
            .setDuration(200)
            .withEndAction {
                text.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
    }

    private fun requestLocationPermission() {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)

            }
        }
    }

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

    private fun setupLocation() {
        createLocationRequest()
        startLocationUpdates()
    }

    private fun updateLocation(location: Location?) {
        if (location != null) {
            Log.i(TAG, "location, lat: ${location.latitude} long: ${location.longitude}")

            val userLatLng = LatLng(location.latitude, location.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, ZOOM_LEVEL))

            // initialize lastLocation
            if (!::lastLocation.isInitialized)
                lastLocation = location
            // if move far enough, remove all map item
            else if (location.distanceTo(lastLocation) > 50f) {
                lastLocation = location
                items_layout.removeAllViews()
            }

            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)

                val curItemCount = items_layout.childCount
                if (curItemCount <= MAX_MAP_ITEM_COUNT)
                    for (i in curItemCount..MAX_MAP_ITEM_COUNT)
                    // 1/10 chance of chest
                        addMapItem(Random.nextInt(10) != 0)
            }

        } else {
            Log.i(TAG, "NULL LOCATION")
        }
    }


    private fun addMapItem(monster: Boolean): View {
        val r = Random
        val h = items_layout.height
        val w = items_layout.width

        val x = r.nextInt(0,w - 200).toFloat()
        val y = r.nextInt(0,h/2).toFloat()

        // TODO make item as Marker
        // TODO item intersection check

        val name = if (monster) "Slime Monster" else "Chest"
        val view = MapItemView(
            applicationContext,
            monster,
            name,
            lastId++
        )

        view.setOnClickListener {
            //                Toast.makeText(applicationContext, "This is something yo! x: ${x} y: ${y}, h: ${h}, w: ${w}", Toast.LENGTH_SHORT).show()
            if (monster)
                startActivityForResult(
                    Intent(this, BattleActivity::class.java)
                        .putExtra(MONSTER_ID_KEY, view._id),
                    BATTLE_REQUEST_CODE)
            else
                openChest(view._id)
        }

        TransitionManager.beginDelayedTransition(items_layout, android.transition.Fade())
        items_layout.addView(view)

        view.x = x
        view.y = y

        return view
    }

    private fun openChest(_id: Int) {
        player.addExp(1)
        updatePlayer()
        AlertDialog.Builder(this)
            .setTitle("Chest Opened!")
            .setMessage("\nYou get: \n\n1 gold\n1 exp")
            .setPositiveButton("Continue") {
                    _,_ ->
                removeMapItem(_id)
            }
            .show()
    }

    private fun updatePlayer() {
        with (player.getPlayer()) {
            level_text.text = "Level: ${level}"
            player_name.text = "${name}"
            hp_info.text = "${hp}/${hp}"
            mp_info.text = "${mp}/${mp}"

            val nextLevel = level * 10
            val expProgress = exp / nextLevel * 100
            exp_info.text = "${exp}/${nextLevel}"
            exp_bar.progress = expProgress
        }
    }

    private fun removeMapItem(_id: Int) {
        for (i in 0..items_layout.childCount) {
            var v = items_layout.getChildAt(i) as MapItemView
            if (v._id == _id) {
                items_layout.removeView(v)
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BATTLE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val _id = data?.getIntExtra(MONSTER_ID_KEY, -1)
                    if (_id != null && _id != -1) {
                        removeMapItem(_id)
                    }
                    player.addExp(1)
                    updatePlayer()
                } else {

                }
            }
        }
    }


    private fun requestPermission() =
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    private fun isLocationAllowed(): Boolean =
        ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

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
                            REQUEST_CHECK_SETTINGS
                        )
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
        const val LOCATION_PERMISSION_REQUEST_CODE = 0
        const val BATTLE_REQUEST_CODE = 1
        const val REQUEST_CHECK_SETTINGS = 2

        const val TAG = "RPG-RUNNER"

        const val MONSTER_ID_KEY = "monster_id"

        const val ZOOM_LEVEL: Float = 18.0f
        const val MAX_MAP_ITEM_COUNT = 5
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        return true
    }

}
