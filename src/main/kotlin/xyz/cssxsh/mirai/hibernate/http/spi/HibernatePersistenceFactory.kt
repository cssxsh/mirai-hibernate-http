package xyz.cssxsh.mirai.hibernate.http.spi

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.spi.persistence.*

public class HibernatePersistenceFactory : PersistenceFactory {
    override fun getName(): String = "hibernate"

    override fun getService(bot: Bot): Persistence {
        return HibernatePersistence(botId = bot.id)
    }
}