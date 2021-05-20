package repo

import model.main_classes.User

class UserRepoMap : Repo<User> {

    private var maxId = 0
    private val _repo = HashMap<Int, User>()

    override fun create(element: User) =
            if (_repo.containsValue(element))
                false
            else {
                val newId = maxId++
                element.id = newId
                _repo[newId] = element
                true
            }

    override fun read(id: Int) =
            _repo[id]

    override fun read() =
            _repo.values.toList()

    override fun update(id: Int, element: User) =
            if (_repo.containsKey(id)) {
                _repo[id] = element
                true
            } else
                false

    override fun delete(id: Int) =
            if (_repo.containsKey(id)) {
                _repo.remove(id)
                true
            } else
                false
}