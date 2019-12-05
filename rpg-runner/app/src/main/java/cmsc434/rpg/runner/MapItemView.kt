package cmsc434.rpg.runner

import android.content.Context
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import cmsc434.rpg.runner.entity.Enemy
import kotlinx.android.synthetic.main.icon_map_item.view.*

class MapItemView (context: Context, var monster: Boolean, var name: String, val _id: Int): ConstraintLayout(context) {

    init {
        inflate(context, R.layout.icon_map_item, this)

        val imageId = if (monster) R.drawable.icon_slime_monster else R.drawable.chest_icon

        item_image.setImageResource(imageId)
        if (!monster)
            item_image.setColorFilter(Color.MAGENTA)
        item_name.text = name
    }
}