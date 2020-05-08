import org.jetbrains.exposed.sql.Database
import persistence.MongoDBUserRepository
import persistence.MySqlUserRepository

object Config {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    private val database by lazy {
        Database.connect(url = System.getenv("MYSQL_URL"), driver = "com.mysql.cj.jdbc.Driver")
    }
    val userRepoMySql by lazy {
        MySqlUserRepository(database).apply { updateSchema() }
    }
    val userRepoMongoDb by lazy {
        MongoDBUserRepository(
            System.getenv("MONGODB_HOST"),
            System.getenv("MONGODB_PORT").toInt(),
            "clean_demo"
        )
    }
}
