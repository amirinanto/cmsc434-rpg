package cmsc434.rpg.runner.entity

data class Enemy(val name: String,
                 var hp: Int = 100,
                 var mp: Int = 0,
                 var exp: Int = 10,
                 var gold: Int = 10,
                 var items: List<Item> = ArrayList())