package cmsc434.rpg.runner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TrackingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
    }
}
