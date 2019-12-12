package cmsc434.rpg.runner


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cmsc434.rpg.runner.helper.PlayerHelper
import cmsc434.rpg.runner.helper.PrefHelper
import cmsc434.rpg.runner.helper.player
import cmsc434.rpg.runner.helper.pref
import android.os.Bundle
import android.view.LayoutInflater
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.widget.*
import kotlinx.android.synthetic.main.activity_character.*


class CharacterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)
        button.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.activity_character2, null)
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )
            popupWindow.elevation = 10.0F
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popupWindow.exitTransition = slideOut

            val buttonPopup = view.findViewById<Button>(R.id.button_popup)
            buttonPopup.setOnClickListener {
                popupWindow.dismiss()
            }
            popupWindow.setOnDismissListener {
                Toast.makeText(applicationContext, "Popup closed", Toast.LENGTH_SHORT).show()
            }

            TransitionManager.beginDelayedTransition(root_layout)
            popupWindow.showAtLocation(
                root_layout,
                Gravity.CENTER,
                0,
                0
            )

        }
    }
}