package cmsc434.rpg.runner.helper

import android.content.Context
import android.content.SharedPreferences

class PrefHelper private constructor(context: Context) {

    private var sharedPref: SharedPreferences

    companion object {

        private var instance: PrefHelper? = null
        @Synchronized
        fun getInstance(context: Context) = instance
            ?: PrefHelper(context.applicationContext)
    }

    init {
        sharedPref = context.getSharedPreferences(PlayerHelper.PREF_FILE, Context.MODE_PRIVATE)
    }

    fun putSettingPlease(key: String, value: Any) {
        with (sharedPref.edit()) {
            when (value) {
                is Int -> putInt(key, value)
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value as Boolean)
                else -> throw Exception()
            }
            commit()
        }
    }

    fun getSettingPlease(key: String, default: Any): Any {
        var value: Any
        with (sharedPref){
            value = when (default) {
                is Int -> getInt(key, default)
                is String -> getString(key, default) ?: "NULL"
                is Boolean -> getBoolean(key, default)
                else -> default
            }
        }
        return value
    }

    fun isSettingExist(key: String): Boolean = sharedPref.contains(key)
}

val Context.pref: PrefHelper
    get() = PrefHelper.getInstance(
        this
    )