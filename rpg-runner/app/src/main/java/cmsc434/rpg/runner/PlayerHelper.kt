package cmsc434.rpg.runner

import android.content.Context
import android.content.SharedPreferences
import cmsc434.rpg.runner.entity.Player

class PlayerHelper private constructor(context: Context) {

    private lateinit var sharedPref: SharedPreferences

    companion object {
        const val PREF_FILE = "rpg_runner"

        const val PLAYER_NAME = "name"
        const val PLAYER_LEVEL = "level"
        const val PLAYER_EXP = "exp"
        const val PLAYER_HP = "hp"
        const val PLAYER_MAX_HP = "max_hp"
        const val PLAYER_MP = "mp"
        const val PLAYER_MAX_MP = "max_mp"

        private var instance: PlayerHelper? = null
        @Synchronized
        fun getInstance(context: Context) = instance ?: PlayerHelper(context.applicationContext)
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
            putInt(PLAYER_MAX_HP, 10)
            putInt(PLAYER_MP, 5)
            putInt(PLAYER_MAX_MP, 10)
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
            levelUp = 1

        with (sharedPref!!.edit()) {
            putInt(PLAYER_EXP, exp)

            if (levelUp > 0) {
                level += levelUp
                putInt(PLAYER_LEVEL, level)
                putInt(PLAYER_HP, level * 10)
                putInt(PLAYER_MP, level * 10)
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

    fun isExist(): Boolean {
        return sharedPref.contains(PLAYER_NAME)
    }

    fun getPlayer(): Player {
        var p = Player("ERROR")
        if (sharedPref != null) {
            p = Player(sharedPref.getString(PLAYER_NAME, "ERROR")!!,
                level = sharedPref.getInt(PLAYER_LEVEL, 1),
                exp = sharedPref.getInt(PLAYER_EXP, 1),
                hp = sharedPref.getInt(PLAYER_HP, 1),
                mp = sharedPref.getInt(PLAYER_MP, 1))
        }
        return p
    }
}

val Context.player: PlayerHelper get() = PlayerHelper.getInstance(this)