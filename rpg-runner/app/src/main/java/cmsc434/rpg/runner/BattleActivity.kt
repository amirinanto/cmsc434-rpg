package cmsc434.rpg.runner

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_battle.*

class BattleActivity : AppCompatActivity() {

    var exit = false
    var actionBeingPerformed = false
    var skillMode = false
    var itemMode = false
    var returnIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        val monsterId = intent.getIntExtra(AdventureActivity.MONSTER_ID_KEY, -1)
        if (monsterId != -1)
            returnIntent.putExtra(AdventureActivity.MONSTER_ID_KEY, monsterId)

        enemy_name.text = "Slime Monster"

        fab_close.setOnClickListener {
            if (skillMode) {
                skillMode = false
                switchCards()
                return@setOnClickListener
            } else if (itemMode) {
                itemMode = false
                switchCards()
                return@setOnClickListener
            }
            flee()
        }

        attack_button.setOnClickListener { attack() }

        skill_button.setOnClickListener { skill() }

        skill1_button.setOnClickListener {
            animateBattleStatus("Player use Fire\nMonster loses 10 HP\nMonster defeated")
            damage_layout.setBackgroundColor(Color.RED)
            damageEnemy()
        }

        skill2_button.setOnClickListener {
            animateBattleStatus("Player use Ice\nMonster loses 10 HP\nMonster defeated")
            damage_layout.setBackgroundColor(Color.CYAN)
            damageEnemy()
        }

        defend_button.setOnClickListener { defend() }

        item_button.setOnClickListener { item() }

        item1_button.setOnClickListener {
            animateBattleStatus("Player use Lucky Charm\nNothing happened..\nMonster defeated")
        }

        item2_button.setOnClickListener {
            animateBattleStatus("Player throw Torch\nMonster loses 10 HP\nMonster defeated")
            damage_layout.setBackgroundColor(Color.RED)
            damageEnemy()
        }
    }

    private fun switchCards() {
        val hide = View.GONE
        val show = View.VISIBLE

        if (skillMode) {
            item_action_card.visibility = hide
            primary_action_card.visibility = hide
            skill_action_card.visibility = show

        } else if (itemMode) {
            item_action_card.visibility = show
            primary_action_card.visibility = hide
            skill_action_card.visibility = hide

        } else {
            item_action_card.visibility = hide
            primary_action_card.visibility = show
            skill_action_card.visibility = hide

        }
    }

    private fun flee() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun skill() {
        skillMode = true
        switchCards()
    }

    private fun attack() {
        animateBattleStatus("Player use Attack\nMonster loses 10 HP\nMonster defeated")
        damage_layout.setBackgroundColor(Color.GRAY)
        damageEnemy()
    }

    private fun damageEnemy() {
        damage_text.visibility = View.VISIBLE
        enemy_health.text = "0/10"

        damage_layout.animate()
            .alpha(0.5f)
            .setDuration(1000)
            .withEndAction {
                damage_layout.animate()
                    .alpha(0f)
                    .setDuration(1000)
                    .start()
            }.start()

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
            .setMessage("Rewards:\n\n1 exp\n1 gold")
            .setPositiveButton("Continue") {
                _,_ ->
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            .setCancelable(false)
            .show()

    }

    private fun item() {
        itemMode = true
        switchCards()
    }

    private fun defend() {
        animateBattleStatus("Player use Defend\nMonster use Defend")
    }

    private fun setPrimaryButtons(isEnabled: Boolean) {

        val gray = Color.GRAY
        val blue = ContextCompat.getColor(applicationContext, R.color.colorPrimary)

        val color = if (isEnabled) blue else gray

        val buttons = arrayOf<View>(attack_button,
            item_button,
            skill_button,
            defend_button,
            item1_button,
            item2_button,
            skill1_button,
            skill2_button)

        for (v in buttons) {
            v.setBackgroundColor(color)
            v.isClickable = isEnabled
        }

        fab_close.isVisible = isEnabled

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
