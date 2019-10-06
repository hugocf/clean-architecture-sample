package persistence

import com.mongodb.MongoWriteException
import com.mongodb.client.model.IndexOptions
import domain.EmailAddress
import domain.User
import domain.UserRepository
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

class MongoDBUserRepository(host: String, port: Int, database: String) : UserRepository {

    private val usersColection = KMongo
        .createClient(host, port)
        .getDatabase(database)
        .getCollection<UserSchema>("users")

    private data class UserSchema(
        var id: String,
        val email: String,
        val name: String,
        var hashedPassword: String
    )

    override fun createSchema() {
        usersColection.createIndex(Document("email", 1), IndexOptions().unique(true))
    }

    override fun findAll() =
        usersColection.find().toList().map {
            User(id = it.id, email = EmailAddress(it.email), name = it.name, hashedPassword = it.hashedPassword)
        }

    override fun save(user: User) {
        try {
            usersColection.save(
                UserSchema(
                    id = user.id ?: throw RuntimeException("missing id"),
                    email = user.email.value,
                    name = user.name,
                    hashedPassword = user.hashedPassword ?: throw RuntimeException("missing hashed password")
                )
            )
        } catch (ex: MongoWriteException) {
            throw if (ex.message!!.contains("email_1 dup key")) User.UserAlreadyExists() else ex
        }
    }

    override fun deleteAll() {
        usersColection.deleteMany(Document())
    }
}