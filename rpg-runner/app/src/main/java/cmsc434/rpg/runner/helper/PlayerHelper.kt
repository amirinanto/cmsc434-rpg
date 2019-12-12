package cmsc434.rpg.runner.helper

import android.content.Context
import android.content.SharedPreferences
import cmsc434.rpg.runner.entity.Player

class PlayerHelper private constructor(context: Context) {

    private var sharedPref: SharedPreferences

    companion object {
        const val PREF_FILE = "rpg_runner"

        const val PLAYER_NAME = "name"
        const val PLAYER_LEVEL = "level"
        const val PLAYER_EXP = "exp"
        const val PLAYER_HP = "hp"
        const val PLAYER_MP = "mp"
        const val PLAYER_MILES = "miles"

        private var instance: PlayerHelper? = null
        @Synchronized
        fun getInstance(context: Context) = instance
            ?: PlayerHelper(context.applicationContext)
    }

    init {
        sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    fun initPlayer(name: String) {
        with (sharedPref.edit()) {
            putString(PLAYER_NAME, name)
            putInt(PLAYER_LEVEL, 1)
            putInt(PLAYER_EXP, 0)
            putInt(PLAYER_HP, 10)
            putInt(PLAYER_MP, 5)
            putFloat(PLAYER_MILES, 0f)
            commit()
        }
    }

    fun addExp(addExp: Int) {
        var level = sharedPref.getInt(PLAYER_LEVEL, 1)
        var exp = sharedPref.getInt(PLAYER_EXP, 0)
        var levelUp = 0

        val nextLv = exp + (10 - exp % 10)
        exp += addExp
        if (exp >= nextLv)
            levelUp = addExp / 10

        with (sharedPref!!.edit()) {
            putInt(PLAYER_EXP, exp)

            if (levelUp > 0) {
                level += levelUp
                putInt(PLAYER_LEVEL, level)
                putInt(PLAYER_HP, level * 10)
                putInt(PLAYER_MP, level * 5)
            }
            commit()
        }
    }

    fun changeHp(hp: Int) {
        with(sharedPref!!.edit()) {
            putInt(PLAYER_HP, hp)
            commit()
        }
    }

    fun changeMp(mp: Int) {
        with (sharedPref!!.edit()) {
            putInt(PLAYER_MP, mp)
            commit()
        }
    }

    fun addMiles(miles: Float) {
        if (miles >= 0.1) {
            var playerMiles = sharedPref.getFloat(PLAYER_MILES, 0f)

            with(sharedPref.edit()) {
                playerMiles += miles
                putFloat(PLAYER_MILES, playerMiles)
                commit()
            }

            var reward = (miles * 10).toInt()
            addExp(reward)
        }
    }

    fun isExist(): Boolean {
        return sharedPref.contains(PLAYER_NAME)
    }

    fun getPlayer(): Player {
        var p = Player("ERROR")
        if (sharedPref != null) {
            with (sharedPref) {
                p = Player(getString(PLAYER_NAME, "ERROR")!!,
                    level = getInt(PLAYER_LEVEL, 1),
                    exp = getInt(PLAYER_EXP, 1),
                    hp = getInt(PLAYER_HP, 1),
                    mp = getInt(PLAYER_MP, 1),
                    miles = getFloat(PLAYER_MILES, 0f))
            }
        }
        return p
    }
}

val Context.player: PlayerHelper
    get() = PlayerHelper.getInstance(
        this
    )