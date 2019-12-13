package cmsc434.rpg.runner.list

data class Mission(val id: Int,
                   val title: String,
                   val desc: String,
                   val reqString: String,
                   val reqInt: Int,
                   val rewardString: String,
                   val rewardInt: Int,
                   var isDone: Boolean = false,
                   var isStory: Boolean = false)