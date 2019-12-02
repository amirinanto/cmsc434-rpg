package cmsc434.rpg.runner.entity

data class Player(val name: String,
                  var level: Int = 0,
                  var exp: Int = 0,
                  var gold: Int = 0,
                  var miles: Float = 0f,
                  var hp: Int = 0,
                  var mp: Int = 0) {

    val items = ArrayList<Item>()
}