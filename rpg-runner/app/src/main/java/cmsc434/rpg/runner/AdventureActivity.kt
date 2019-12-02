package cmsc434.rpg.runner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions

import kotlinx.android.synthetic.main.activity_adventure.*

class AdventureActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private var zoomLevel: Float = 18.0f
    private var lastId: Int = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure)

        fab_menu.setOnClickListener {

        }

        fab_back.setOnClickListener {
            finish()
        }

        (supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment)
            .getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestLocationPermission()
        }

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        map.apply {
            isMyLocationEnabled = true
            setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext,  R.raw.night_map))
            uiSettings.setAllGesturesEnabled(false)
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                updateLocation(it)
                addMapItem(true, "Slime", 0f, 0f)
            }
            .addOnCanceledListener {
                Toast.makeText(applicationContext, "canceled", Toast.LENGTH_LONG).show() }
            .addOnCompleteListener{
                Toast.makeText(applicationContext, "completed" + it.toString(), Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(applicationContext, "failure" + it.toString(), Toast.LENGTH_LONG).show()
            }


    }

    private fun updateLocation(location: Location?) {
        if (location != null) {
            Log.i(TAG, "location, lat: ${location.latitude} long: ${location.longitude}")

            val curLatLng = LatLng(location.latitude, location.longitude)

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, zoomLevel))
        } else {
            Log.i(TAG, "NULL LOCATION")
        }
    }

    private fun addMapItem(monster: Boolean, name: String, x: Float, y: Float): View {
        val view = MapItemView(applicationContext, monster, name, lastId++)

        view.setOnClickListener {
            if (view.monster) {
                Toast.makeText(applicationContext, "This is monster yo!", Toast.LENGTH_SHORT).show()
            }
        }

        items_layout.addView(view)

        view.x = x
        view.y = y

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BATTLE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                }
            }
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 0
        const val BATTLE_REQUEST_CODE = 1

        const val TAG = "RPG-RUNNER"

        const val TYPE_MONSTER = 1
        const val TYPE_CHEST = 2
    }

}
