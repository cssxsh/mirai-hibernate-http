package xyz.cssxsh.mirai.hibernate.http.spi

import net.mamoe.mirai.api.http.spi.persistence.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.hibernate.*
import xyz.cssxsh.mirai.hibernate.entry.*

public class HibernatePersistence(private val botId: Long) : Persistence {

    @Suppress("INVISIBLE_MEMBER")
    private val factory: org.hibernate.SessionFactory get() = xyz.cssxsh.mirai.hibernate.factory

    override fun getMessage(context: Context): MessageSource {
        return getMessageOrNull(context)
            ?: throw NoSuchElementException("ids: ${context.ids.contentToString()}, subject: ${context.subject}")
    }

    override fun getMessageOrNull(context: Context): MessageSource? {
        val records = factory.fromSession { session ->
            session.withCriteria<MessageRecord> { query ->
                val record = query.from<MessageRecord>()
                query.select(record)
                    .where(
                        equal(record.get<Long>("bot"), botId),
                        equal(record.get<String>("targetId"), context.subject.id),
                        equal(record.get<String>("ids"), context.ids.joinToString()),
                    )
            }.resultList
        }

        return records.maxByOrNull { it.time }?.toMessageSource()
    }

    override fun onMessage(messageSource: OnlineMessageSource) {
        // ...
    }
}