package cmsc434.rpg.runner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        loadStoryMissions()
    }

    private fun loadStoryMissions() {
        missionList = ArrayList()

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

        missionAdapter = MissionAdapter(missionList) {

        }

        mission_list.layoutManager = LinearLayoutManager(applicationContext)
        mission_list.adapter = missionAdapter

    }
}
