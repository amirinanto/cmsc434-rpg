package cmsc434.rpg.runner

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_battle.*

class BattleActivity : AppCompatActivity() {

    var exit = false
    var actionBeingPerformed = false
    var returnIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        val monsterId = intent.getIntExtra(AdventureActivity.MONSTER_ID_KEY, -1)
        if (monsterId != -1)
            returnIntent.putExtra(AdventureActivity.MONSTER_ID_KEY, monsterId)

        enemy_name.text = "Slime Monster"

        fab_close.setOnClickListener { flee() }

        attack_button.setOnClickListener { attack() }

        skill_button.setOnClickListener { skill() }

        defend_button.setOnClickListener { defend() }

        item_button.setOnClickListener { item() }
    }

    private fun flee() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun skill() {

    }

    private fun attack() {
        animateBattleStatus("Player use Attack\nMonster loses 10 HP\nMonster defeated")
        damage_text.visibility = View.VISIBLE
        enemy_health.text = "0/10"
        damage_text.animate()
            .scaleX(2f)
            .scaleY(2f)
            .setDuration(2000)
            .withEndAction {
                damage_text.animate()
                    .scaleY(1f)
                    .scaleX(1f)
                    .setDuration(0)
                    .start()
                damage_text.visibility = View.GONE
                enemyDefeated()
            }
            .start()
    }

    private fun enemyDefeated() {
        AlertDialog.Builder(this)
            .setTitle("Victory!")
            .setMessage("Rewards:\n\n100 exp\n100 gold")
            .setPositiveButton("Continue") {
                _,_ ->
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }.show()

    }

    private fun item() {

    }

    private fun defend() {
        animateBattleStatus("Player use Defend\nMonster use Defend")
    }

    private fun setPrimaryButtons(isEnabled: Boolean) {

        val gray = Color.GRAY
        val blue = ContextCompat.getColor(applicationContext, R.color.colorPrimary)

        val color = if (isEnabled) blue else gray

        attack_button.setBackgroundColor(color)
        attack_button.isClickable = isEnabled

        skill_button.setBackgroundColor(color)
        skill_button.isClickable = isEnabled

        defend_button.setBackgroundColor(color)
        defend_button.isClickable = isEnabled

        item_button.setBackgroundColor(color)
        item_button.isClickable = isEnabled

        fab_close.setBackgroundColor(color)
        fab_close.isClickable = isEnabled

        actionBeingPerformed = true
    }

    private fun defaultBatleStatus()  = resources.getText(R.string.player_turn).toString()


    private fun animateBattleStatus(newText: String) {
        setPrimaryButtons(false)
        battle_status.text = newText
        battle_status.animate()
            .alpha(.5f)
            .setDuration(1000)
            .withEndAction {
                battle_status.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .withEndAction {
                        battle_status.text = defaultBatleStatus()
                        setPrimaryButtons(true)
                    }
                    .start()
            }.start()
    }

    override fun onBackPressed() {
        if (actionBeingPerformed)
            return

        if (exit)
            flee()

        exit = true
        Toast.makeText(applicationContext,
            "Press back again or \"X\" button to exit.",
            Toast.LENGTH_SHORT).show()
    }

    companion object {

    }
}
