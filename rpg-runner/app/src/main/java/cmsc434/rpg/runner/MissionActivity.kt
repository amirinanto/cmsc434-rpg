package cmsc434.rpg.runner

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cmsc434.rpg.runner.list.Mission
import cmsc434.rpg.runner.list.MissionAdapter
import kotlinx.android.synthetic.main.activity_mission.*

class MissionActivity : AppCompatActivity() {

    private lateinit var missionList: ArrayList<Mission>
    private lateinit var missionAdapter: MissionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)

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

        val mission_desc = resources.getStringArray(R.array.story_missions_desc)
        val mission_title = resources.getStringArray(R.array.story_missions_title)
        val mission_req = resources.getStringArray(R.array.story_missions_req)
        val mission_reward = resources.getStringArray(R.array.story_missions_rewards)

        for (i in 0..2) {
            missionList.add(Mission(i,
                mission_title[i],
                mission_desc[i],
                mission_req[i],
                (i+1)*100,
                mission_reward[i],
                (i+1)*200))
        }

        missionAdapter.notifyDataSetChanged()
    }

}
