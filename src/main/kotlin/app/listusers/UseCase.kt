package app.listusers

class UseCase(private val repository: Repository) {
    fun list() = repository.list()
}