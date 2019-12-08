package cmsc434.rpg.runner

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cmsc434.rpg.runner.helper.PlayerHelper
import cmsc434.rpg.runner.helper.PrefHelper
import cmsc434.rpg.runner.helper.player
import cmsc434.rpg.runner.helper.pref
import cmsc434.rpg.runner.list.Mission
import cmsc434.rpg.runner.list.MissionAdapter
import kotlinx.android.synthetic.main.activity_mission.*

class MissionActivity : AppCompatActivity() {

    private lateinit var missionList: ArrayList<Mission>
    private lateinit var missionAdapter: MissionAdapter
    private lateinit var pref: PrefHelper
    private lateinit var player: PlayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)

        pref = applicationContext.pref
        player = applicationContext.player

        story_button.setOnClickListener {
            loadStoryMissions()
        }

        daily_button.setOnClickListener {
            loadDailyMissions()
        }

        fab_back.setOnClickListener {
            finish()
        }

        missionList = ArrayList()
        missionAdapter = MissionAdapter(missionList) {
            val id = it.id

            if (id > 0) {
                val isPrefStoryDone = pref.getSettingPlease(STORY_DONE[id - 1], false) as Boolean
                if (isPrefStoryDone) {
                    Toast.makeText(this, "Please complete previous mission!", Toast.LENGTH_SHORT).show()
                    return@MissionAdapter
                }

            }

            startActivityForResult(Intent(this, TrackingActivity::class.java)
                    .putExtra(MISSION_NUM, it.id)
                    .putExtra(MISSION_REQ, it.reqInt)
                    .putExtra(MISSION_REWARD, it.rewardInt),
                    TRACKING_REQUEST_CODE)
        }

        mission_list.layoutManager = LinearLayoutManager(applicationContext)
        mission_list.adapter = missionAdapter

        loadStoryMissions()
    }

    private fun setStorySelected(yes: Boolean) {
        val blue = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        val white = Color.WHITE
        if (yes){
            story_button.setBackgroundColor(blue)
            story_button.setTextColor(white)
            daily_button.setBackgroundColor(white)
            daily_button.setTextColor(blue)
        } else {
            story_button.setBackgroundColor(white)
            story_button.setTextColor(blue)
            daily_button.setBackgroundColor(blue)
            daily_button.setTextColor(white)
        }
    }

    private fun loadDailyMissions() {
        setStorySelected(false)

        missionList.clear()

        for (i in 1..10) {
            val reward = i*100
            missionList.add(
                Mission(i,
                    "RUN ${i} Miles",
                    "Enjoy your run.",
                    "${i} Miles",
                    i*1,
                    "${reward} exp, ${reward} gold",
                    reward))
        }

        missionAdapter.notifyDataSetChanged()
    }


    private fun loadStoryMissions() {
        setStorySelected(true)

        missionList.clear()

        val missionDesc = resources.getStringArray(R.array.story_missions_desc)
        val missionTitle = resources.getStringArray(R.array.story_missions_title)
        val missionReq = resources.getStringArray(R.array.story_missions_req)
        val missionReward = resources.getStringArray(R.array.story_missions_rewards)

        for (i in 0..2) {
            val missionNum = i + 1
            val isDone = pref.getSettingPlease(STORY_DONE[i], false) as Boolean
            missionList.add(Mission(i,
                missionTitle[i],
                missionDesc[i],
                missionReq[i],
                missionNum,
                missionReward[i],
                (missionNum)*200,
                isDone))
        }

        missionAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TRACKING_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val missionNum = data.getIntExtra(MISSION_NUM, 0)
                val mission = missionList[missionNum]
                val miles = data.getFloatExtra(RUN_MILES, 0f)
                val extraReward = data.getIntExtra(RUN_REWARD, 0)

                if (miles > mission.reqInt) {
                    pref.putSettingPlease(STORY_DONE[missionNum], true)
                    mission.isDone = true
                    missionAdapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(this, "Mission Requirement is unfulfilled!", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Don't worry you are still rewarded :)", Toast.LENGTH_SHORT).show()
                }

                player.addMiles(miles)

                if (extraReward > 0)
                    player.addExp(extraReward)

            } else {
                Toast.makeText(this, "Not enough run to get any reward!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TRACKING_REQUEST_CODE = 0

        const val MISSION_NUM = "mission_num"
        const val MISSION_REQ = "mission_key"
        const val MISSION_REWARD = "mission_reward"
        const val MISSION_DONE = "mission_done"
        const val RUN_MILES = "run_miles"
        const val RUN_REWARD = "run_reward"

        val STORY_DONE = arrayOf("story1", "story2", "story3")
    }

}
