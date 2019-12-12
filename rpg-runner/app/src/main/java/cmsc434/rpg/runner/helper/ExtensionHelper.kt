package cmsc434.rpg.runner.helper

import android.content.Context
import android.media.MediaPlayer
import cmsc434.rpg.runner.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions


fun GoogleMap.setCustomMapStyle(style: MapStyleOptions) {
    isMyLocationEnabled = true
    setMapStyle(style)
    uiSettings.setAllGesturesEnabled(false)
}

fun Context.getMapStyle(pref: PrefHelper): MapStyleOptions {
    val mapStyle =
        if (pref.isSettingExist("map_setting"))
            when (pref.getSettingPlease("map_setting", "night")) {
                "night" -> R.raw.night_map
                "dark" -> R.raw.dark_map
                "retro" -> R.raw.retro_map
                "silver" -> R.raw.silver_map
                else -> R.raw.night_map }
        else R.raw.night_map

    return MapStyleOptions.loadRawResourceStyle(applicationContext,  mapStyle)
}

val Context.music: MediaPlayer
    get() = MediaPlayer.create(this.applicationContext, R.raw.bg_music)