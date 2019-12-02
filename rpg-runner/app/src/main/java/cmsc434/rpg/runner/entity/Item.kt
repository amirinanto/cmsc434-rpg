package cmsc434.rpg.runner.entity

data class Item(val name: String,
                val effectStr: String,
                val effectFun: (Player, Enemy) -> Unit,
                var quantity: Int = 0)