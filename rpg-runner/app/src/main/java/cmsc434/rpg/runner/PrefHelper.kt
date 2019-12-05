package cmsc434.rpg.runner

import android.content.Context
import android.content.SharedPreferences

class PrefHelper private constructor(context: Context) {

    private var sharedPref: SharedPreferences

    companion object {
        private var instance: PrefHelper? = null
        @Synchronized
        fun getInstance(context: Context) = instance ?: PrefHelper(context.applicationContext)
    }

    init {
        sharedPref = context.getSharedPreferences(PlayerHelper.PREF_FILE, Context.MODE_PRIVATE)
    }

    fun putSettingPlease(key: String, value: Any) {
        with (sharedPref.edit()) {
            when (value) {
                Int -> putInt(key, value as Int)
                String -> putString(key, value as String)
                Boolean -> putBoolean(key, value as Boolean)
                else -> throw Exception()
            }
            commit()
        }
    }

    fun getSettingPlease(key: String, default: Any): Any {
        var value: Any
        with (sharedPref){
            value = when (default) {
                Int -> getInt(key, default as Int)
                String -> getString(key, default as String) ?: "NULL"
                Boolean -> getBoolean(key, default as Boolean)
                else -> throw Exception()
            }
        }
        return value
    }
}

val Context.pref: PrefHelper get() = PrefHelper.getInstance(this)