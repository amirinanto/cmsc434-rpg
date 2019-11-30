package cmsc434.rpg.runner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)
    }
}
