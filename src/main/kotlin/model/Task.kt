package model

interface Task {
    val name: String
    val description: String
    val difficulty: TaskDifficulty
    val rewards: Reward
    val type: TaskType
    val difficultyToInt: Int
        get() = when(difficulty){
            TaskDifficulty.Very_easy -> 1
            TaskDifficulty.Easy -> 2
            TaskDifficulty.Medium -> 3
            TaskDifficulty.Hard -> 4
            TaskDifficulty.Veru_Hard -> 5
        }
}