package model

import items.Item

class Reward(
    val experiencePoints: Int,
    val coins: Int,
    val items: List<Item>?
){
    fun getReward(character: Character){
        character.experiencePoints+=experiencePoints
        character.checkExperience()
        character.coins+=coins
        items?.forEach { item ->
            if (character.personalRewards.find{it.name == item.name} != null) {
                character.personalRewards.find { it.name ==  item.name}!!.quantity+=item.quantity
            }else character.personalRewards.add(item)
        }
    }
    fun getGreedyReward(character: Character){
        character.experiencePoints+=(experiencePoints*1.5).toInt()
        character.checkExperience()
        character.coins+= (coins*1.5).toInt()
        if (items!=null) character.personalRewards.addAll(items)
    }
}