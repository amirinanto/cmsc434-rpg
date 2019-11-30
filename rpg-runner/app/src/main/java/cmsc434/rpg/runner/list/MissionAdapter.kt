package cmsc434.rpg.runner.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cmsc434.rpg.runner.R
import kotlinx.android.synthetic.main.item_mission.view.*

class MissionAdapter(val missionList: List<Mission>, val onClick: (Mission) -> Unit) : RecyclerView.Adapter<MissionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_mission, parent, false))

    override fun getItemCount(): Int = missionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
            = holder.onBind(missionList[position], onClick)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(mission: Mission, onClick: (Mission) -> Unit) {
            with (itemView) {
                mission_title.text = mission.title
                mission_desc.text = mission.desc
                mission_req.text = mission.reqString
                mission_reward.text = mission.rewardString

                with (button_start) {
                    if (mission.isDone) {
                        text = resources.getText(R.string.done_mission)
                        isClickable = false
                        alpha = .5f

                    } else {
                        setOnClickListener {
                            onClick(mission)
                        }
                    }
                }
            }
        }
    }

}