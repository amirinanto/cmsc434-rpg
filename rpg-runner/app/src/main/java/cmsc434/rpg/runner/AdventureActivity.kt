package cmsc434.rpg.runner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.transition.Fade
import androidx.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions

import kotlinx.android.synthetic.main.activity_adventure.*
import kotlin.random.Random

class AdventureActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private var zoomLevel: Float = 18.0f
    private var lastId: Int = 0
    private var numOfMonster = 3
    private var numOfChest = 1

    private val pref: PrefHelper = applicationContext.pref
    private val player: PlayerHelper  = applicationContext.player
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

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

    override fun onResume() {
        super.onResume()
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
                updateLocation(it)
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

            for (x in 1..numOfMonster)
                addMapItem(true)

            for (x in 1..numOfChest)
                addMapItem(false)

        } else {
            Log.i(TAG, "NULL LOCATION")
        }
    }


    private fun addMapItem(monster: Boolean): View {
        val r = Random
        val h = items_layout.height
        val w = items_layout.width
        val x = r.nextInt(0,w - 200).toFloat()
        val y = r.nextInt(0,h - 500).toFloat()
        val name = if (monster) "Slime Monster" else "Chest"
        val view = MapItemView(applicationContext, monster, name, lastId++)

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
        val p = player.getPlayer()
        level_text.text = "Level: ${p.level}"
        player_name.text = "${p.name}"
        hp_info.text = "${p.hp}/${p.hp}"
        mp_info.text = "${p.mp}/${p.mp}"
        exp_info.text = "${p.exp}/${p.level*10}"
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

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 0
        const val BATTLE_REQUEST_CODE = 1

        const val TAG = "RPG-RUNNER"

        const val MONSTER_ID_KEY = "monster_id"
    }

}
