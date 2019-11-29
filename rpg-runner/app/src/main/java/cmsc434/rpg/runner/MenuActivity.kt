package cmsc434.rpg.runner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.BounceInterpolator

import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    private var right = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        adventure_button.setOnClickListener {
            var intent = Intent(this, AdventureActivity::class.java)
            startActivity(intent)
        }

        mission_button.setOnClickListener {

        }

        character_button.setOnClickListener {
            var intent = Intent(this, BattleActivity::class.java)
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
            .setDuration(2000)
            .withEndAction {
                moveLeft()
            }
            .start()
    }

    private fun moveLeft() {
        run_animation_block.animate()
            .translationX(-1200f)
            .setDuration(0)
            .withEndAction {
                moveRight()
            }
            .start()
    }
}
