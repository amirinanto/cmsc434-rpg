package cmsc434.rpg.runner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

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
}
