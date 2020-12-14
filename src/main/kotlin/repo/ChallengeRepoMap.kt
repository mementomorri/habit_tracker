package repo

import model.challenges.Challenge


class ChallengeRepoMap {

    private var maxId = 0
    private val _repo = HashMap<Int, Challenge>()

    fun create(element: Challenge) =
            if (_repo.containsValue(element))
                false
            else {
                val newId = maxId++
                _repo[newId] = element
                true
            }

    fun read(id: Int) =
            _repo[id]

    fun read() =
            _repo.values.toList()
}