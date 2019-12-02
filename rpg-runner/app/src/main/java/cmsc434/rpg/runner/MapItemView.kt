package cmsc434.rpg.runner

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.icon_map_item.view.*

class MapItemView (context: Context, var monster: Boolean, var name: String): ConstraintLayout(context) {
    init {
        inflate(context, R.layout.icon_map_item, this)

        val imageId = if (monster) R.drawable.icon_slime_monster else R.drawable.icon_slime_monster

        item_image.setImageResource(imageId)
        item_name.text = name
    }
}