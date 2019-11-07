package cmsc434.rpg.runner

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var zoomLevel: Float = 20.0f

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab_menu.setOnClickListener {
            zoomMap(true)
        }

        fab_run.setOnClickListener {
            zoomMap(false)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestLocationPermission()
        }

        mapFragment.getMapAsync(this)
    }

    private fun zoomMap(zoom: Boolean) {
        mMap.apply {
            val umd = LatLng(38.9858, -76.9373)
            mMap.apply {
                addMarker(MarkerOptions().position(umd).title("Marker in UMD"))
                moveCamera(CameraUpdateFactory.newLatLng(umd))
                if (zoom) {
                    zoomLevel += 1.0f
                } else {
                    zoomLevel -= 1.0f
                }
                Toast.makeText(application, "zoom: " + zoomLevel, Toast.LENGTH_LONG).show()
                moveCamera(CameraUpdateFactory.newLatLngZoom(umd, zoomLevel));
             }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
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
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val umd = LatLng(38.9858, -76.9373)
        mMap.apply {
            addMarker(MarkerOptions().position(umd).title("Marker in UMD"))
            moveCamera(CameraUpdateFactory.newLatLng(umd))
            moveCamera(CameraUpdateFactory.newLatLngZoom(umd, zoomLevel));
            uiSettings.setAllGesturesEnabled(false)
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Toast.makeText(applicationContext, location.toString(), Toast.LENGTH_LONG).show()
            }
            .addOnCanceledListener {
                Toast.makeText(applicationContext, "canceled", Toast.LENGTH_LONG)
            }
            .addOnCompleteListener{
                Toast.makeText(applicationContext, "completed", Toast.LENGTH_LONG)
            }
            .addOnFailureListener{
                Toast.makeText(applicationContext, "failure", Toast.LENGTH_LONG)
            }

    }

}
