package cmsc434.rpg.runner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        adventure_button.setOnClickListener {
            var intent = Intent(this, AdventureActivity::class.java)
            startActivity(intent)
        }
    }
}
