package cmsc434.rpg.runner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import cmsc434.rpg.runner.helper.PlayerHelper
import cmsc434.rpg.runner.helper.player

import kotlinx.android.synthetic.main.activity_menu.*

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

        val p = player.getPlayer()
        player_name.text = p.name
        level_info.text = "Level ${p.level}"
        exp_info.text = "${p.exp}/${p.level*10}"


        adventure_button.setOnClickListener {
            var intent = Intent(this, AdventureActivity::class.java)
            startActivity(intent)
        }

        mission_button.setOnClickListener {
            var intent = Intent(this, MissionActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        moveRight()
    }

    private fun moveRight(){
        run_animation_block.animate()
            .translationX(1200f)
            .setDuration(3000)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                moveLeft()
            }
            .start()
    }

    private fun moveLeft() {
        run_animation_block.animate()
            .translationX(-500f)
            .setDuration(0)
            .withEndAction {
                moveRight()
            }
            .start()
    }
}
