package cmsc434.rpg.runner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import cmsc434.rpg.runner.helper.PlayerHelper
import cmsc434.rpg.runner.helper.player

import kotlinx.android.synthetic.main.activity_menu.*
import java.lang.Exception

class MenuActivity : AppCompatActivity() {

    private var right = false

    private lateinit var player: PlayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        player = applicationContext.player

        if (!player.isExist())
            player.initPlayer("Ryu")


        adventure_button.setOnClickListener {
            var intent = Intent(this, AdventureActivity::class.java)
            startActivity(intent)
        }

        mission_button.setOnClickListener {
            var intent = Intent(this, MissionActivity::class.java)
            startActivity(intent)
        }

        character_button.setOnClickListener{
            try {
                startActivity(Intent(this, OptionActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "This function is not enabled yet :)", Toast.LENGTH_SHORT).show()
            }
        }

        option_button.setOnClickListener {
            try {
                startActivity(Intent(this, OptionActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "This function is not enabled yet :)", Toast.LENGTH_SHORT).show()
            }
        }

        help_button.setOnClickListener {
            try {
                startActivity(Intent(this, HelpActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "This function is not enabled yet :)", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        moveRight()
        updatePlayerInfo()
    }

    private fun updatePlayerInfo() {
        with (player.getPlayer()) {
            player_name.text = name
            level_info.text = "Level ${level} "
            exp_bar2.progress = exp / level * 1000
            exp_info.text = "${exp}/${level*10}"
            miles_info.text = String.format("%.2f", miles)
        }
    }

    private fun moveRight(){
        run_animation_block.animate()
            .translationX(1500f)
            .setDuration(3000)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                hide()
            }
            .start()
    }

    private fun hide() {
        run_animation_block.animate()
            .translationX(-500f)
            .setDuration(0)
            .withEndAction {
                moveRight()
            }
            .start()
    }
}
